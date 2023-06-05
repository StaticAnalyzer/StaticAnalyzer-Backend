package com.staticanalyzer.staticanalyzer.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.staticanalyzer.algservice.AlgAnalyseResult;
import com.staticanalyzer.algservice.AnalyseResponse;
import com.staticanalyzer.algservice.FileAnalyseResults;
import com.staticanalyzer.staticanalyzer.config.project.ProjectProperties;
import com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisResult;
import com.staticanalyzer.staticanalyzer.entity.file.SrcFileAnalysis;
import com.staticanalyzer.staticanalyzer.entity.file.SrcFileDigest;
import com.staticanalyzer.staticanalyzer.entity.file.SrcDirectory;
import com.staticanalyzer.staticanalyzer.entity.file.SrcFile;
import com.staticanalyzer.staticanalyzer.entity.project.Project;
import com.staticanalyzer.staticanalyzer.entity.project.ProjectVO;
import com.staticanalyzer.staticanalyzer.mapper.ProjectMapper;
import com.staticanalyzer.staticanalyzer.service.ProjectService;
import com.staticanalyzer.staticanalyzer.service.error.ServiceError;
import com.staticanalyzer.staticanalyzer.service.error.ServiceErrorType;
import com.staticanalyzer.staticanalyzer.utils.TarGzUtils;

/**
 * 项目服务
 * 上传和查询分析结果等
 * 
 * @author iu_oi
 * @since 0.0.2
 */
@Service
public class ProjectService {

    @Autowired /* project配置属性 */
    private ProjectProperties projectProperties;

    @Autowired /* grpc算法服务 */
    private AlgorithmService algorithmService;

    /*
     * 任务内部类
     * 递交任务的抽象
     */
    @lombok.Setter
    @lombok.Getter
    @AllArgsConstructor
    class Task implements Runnable {

        /* 需要处理的数据 */
        private Project project;

        @Override
        public void run() {
            byte[] sourceCode = project.getSourceCode();
            String config = project.getConfig();
            com.staticanalyzer.algservice.AnalyseResponse analyseResponse = algorithmService.Analyse(sourceCode,
                    config);

            if (project.updateAnalyseResult(analyseResponse)) {
                /* 先删缓存再更新 */
                String hashKey = CACHE_KEY_PROJECT + project.getId();
                redisTemplate.delete(hashKey);
                String listKey = CACHE_KEY_PROJECTVO + project.getUserId();
                redisTemplate.delete(listKey);
                projectMapper.updateById(project);
            }
        }
    }

    /**
     * 线程池
     * 最大{@static 10}
     * 超过则等待
     */
    private ExecutorService taskPool;

    /**
     * 递交项目给算法后端
     * 
     * @param project
     */
    public void submit(Project project) {
        if (taskPool == null)
            taskPool = Executors.newFixedThreadPool(projectProperties.getTaskLimit());
        taskPool.submit(new Task(project));
    }

    @Autowired /* project数据库映射 */
    private ProjectMapper projectMapper;

    /**
     * 新建项目
     * 
     * @apiNote 项目id和上传时间戳会被mysql自动设置
     * @param userId
     * @param sourceCode
     * @param config
     * @return 新建的项目
     * @throws ServiceError
     */
    public Project create(int userId, byte[] sourceCode, String config) throws ServiceError {
        if (sourceCode == null || config == null)
            throw new ServiceError(ServiceErrorType.BAD_PROJECT);

        Project project = new Project();
        project.setUserId(userId);
        project.setSourceCode(sourceCode);
        project.setConfig(config);

        projectMapper.insert(project);
        return project;
    }

    @Autowired /* project模板 */
    private RedisTemplate redisTemplate;

    /* ProjectVO缓存键值前缀 */
    private static String CACHE_KEY_PROJECTVO = "project_of_user:";

    /**
     * 通过所有者id查询项目
     * 
     * @param userId
     * @return 不为{@code null}
     * @see ProjectVO
     */
    public List<ProjectVO> readAll(int userId) {
        String listKey = CACHE_KEY_PROJECTVO + userId;
        List<ProjectVO> projectVOList = redisTemplate.opsForList().range(listKey, 0, -1);
        if (projectVOList.size() > 0) /* 直接读取缓存 */
            return projectVOList;

        /* 从数据库拉取 */
        List<Project> databaseProjectList = projectMapper.selectByUserId(userId);
        projectVOList = databaseProjectList.stream().map(p -> new ProjectVO(p))
                .collect(Collectors.toList());

        /* 写入缓存 */
        if (projectVOList.size() > 0) {
            redisTemplate.opsForList().leftPushAll(listKey, projectVOList);
            redisTemplate.expire(listKey, projectProperties.getExpiration());
        }
        return projectVOList;
    }

    /* Project缓存键值前缀 */
    private static String CACHE_KEY_PROJECT = "project:";

    /**
     * 获取项目文件集
     * 
     * @apiNote 如果分析未完成，则不为FileAnalysis设置分析数据
     * @param project
     * @return 不为{@code null}
     * @throws ServiceError
     * @see SrcFileAnalysis
     */
    private Map<String, SrcFileAnalysis> fetch(int projectId) throws ServiceError {
        String hashKey = CACHE_KEY_PROJECT + projectId;
        Map<String, SrcFileAnalysis> fileAnalyses = redisTemplate.opsForHash().entries(hashKey);
        if (fileAnalyses.size() > 0) /* 直接读取缓存 */
            return fileAnalyses;

        /* 从数据库中拉取 */
        Project databaseProject = projectMapper.selectById(projectId);
        if (databaseProject == null)
            throw new ServiceError(ServiceErrorType.PROJECT_NOT_FOUND);

        Map<String, SrcFile> files;
        try {
            files = TarGzUtils.decompress(databaseProject.getSourceCode());
        } catch (IOException ioException) {
            throw new ServiceError(ServiceErrorType.BAD_PROJECT);
        }

        /*
         * 默认分析结果为空列表
         * 这可能有两种情况，grpc返回错误或不存在对该文件的解析结果
         */
        for (java.util.Map.Entry<String, SrcFile> entry : files.entrySet())
            fileAnalyses.put(entry.getKey(), new SrcFileAnalysis(entry.getValue()));

        /*
         * 拉取分析结果
         * 解析并对fileMap中的条目赋值
         */
        AnalyseResponse analyseResponse = databaseProject.resolveAnalyseResponse();

        if (analyseResponse != null && analyseResponse.getCode() == 0) {
            List<AlgAnalyseResult> algAnalyseResultList = analyseResponse
                    .getAlgAnalyseResultsList();
            /* 遍历每种算法的结果 */
            for (AlgAnalyseResult algAnalyseResult : algAnalyseResultList) {
                if (algAnalyseResult.getCode() != 0)
                    continue;
                /*
                 * 每种算法中都有一个针对单文件的列表
                 * 文件以Map的形式组织
                 */
                for (Map.Entry<String, FileAnalyseResults> entry : algAnalyseResult
                        .getFileAnalyseResultsMap().entrySet()) {
                    /* 附加该算法针对该文件的结果 */
                    SrcFileAnalysis fileAnalysis = fileAnalyses.get(entry.getKey());
                    List<AnalysisResult> newAnalysisResultList = entry.getValue().getAnalyseResultsList().stream()
                            .map(r -> new AnalysisResult(r)).collect(Collectors.toList());
                    if (newAnalysisResultList.size() > 0)
                        fileAnalysis.getAnalyseResults().addAll(newAnalysisResultList);
                }
            }
        }

        /* 写入缓存 */
        if (fileAnalyses.size() > 0) {
            redisTemplate.opsForHash().putAll(hashKey, fileAnalyses);
            redisTemplate.expire(hashKey, projectProperties.getExpiration());
        }
        return fileAnalyses;
    }

    /**
     * 通过项目id和文件路径查询文件分析信息
     * 
     * @param userId
     * @param projectId
     * @return 不为{@code null}
     * @throws ServiceError
     * @see FileAnalysisVO
     */
    public SrcFileAnalysis readFile(int projectId, String filePath) throws ServiceError {
        Map<String, SrcFileAnalysis> files = fetch(projectId);
        SrcFileAnalysis fileAnalysis = files.get(filePath);
        if (fileAnalysis == null)
            throw new ServiceError(ServiceErrorType.FILE_NOT_FOUND);
        return fileAnalysis;
    }

    /**
     * 通过项目id查询项目结构
     * 
     * @param projectId
     * @return 不为{@code null}
     * @throws ServiceError
     * @see SrcFileDigest
     * @see SrcDirectory
     */
    public SrcDirectory read(int projectId) throws ServiceError {
        Map<String, SrcFileAnalysis> files = fetch(projectId);
        SrcDirectory directoryEntry = new SrcDirectory();
        for (Map.Entry<String, SrcFileAnalysis> entry : files.entrySet()) {
            SrcFileDigest fileAnalysisBrief = new SrcFileDigest(entry.getValue());
            directoryEntry.addSrcFile(entry.getKey(), fileAnalysisBrief);
        }
        return directoryEntry;
    }
}
