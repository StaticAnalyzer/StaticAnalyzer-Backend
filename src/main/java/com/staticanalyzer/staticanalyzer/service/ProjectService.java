package com.staticanalyzer.staticanalyzer.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.staticanalyzer.algservice.AlgAnalyseResult;
import com.staticanalyzer.algservice.AnalyseResponse;
import com.staticanalyzer.algservice.FileAnalyseResults;
import com.staticanalyzer.staticanalyzer.config.project.ProjectProperties;
import com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisResult;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisBrief;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisVO;
import com.staticanalyzer.staticanalyzer.entity.project.DirectoryEntry;
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
    @Getter
    @Setter
    @AllArgsConstructor
    class Task implements Runnable {

        /* 需要处理的数据 */
        private Project project;

        @Override
        public void run() {
            byte[] sourceCode = project.getSourceCode();
            String config = project.getConfig();
            AnalyseResponse analyseResponse = algorithmService.Analyse(sourceCode, config);

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
        redisTemplate.opsForList().leftPushAll(listKey, projectVOList);
        redisTemplate.expire(listKey, projectProperties.getExpiration());
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
     * @see FileAnalysis
     */
    private Map<String, FileAnalysis> fetch(int projectId) throws ServiceError {
        String hashKey = CACHE_KEY_PROJECT + projectId;
        Map<String, FileAnalysis> files = redisTemplate.opsForHash().entries(hashKey);
        if (files.size() > 0) /* 直接读取缓存 */
            return files;

        /* 从数据库中拉取 */
        Project databaseProject = projectMapper.selectById(projectId);
        if (databaseProject == null)
            throw new ServiceError(ServiceErrorType.PROJECT_NOT_FOUND);
        try {
            files = TarGzUtils.decompress(databaseProject.getSourceCode());
        } catch (IOException ioException) {
            throw new ServiceError(ServiceErrorType.BAD_PROJECT);
        }

        /* 设置分析结果(如果有) */
        AnalyseResponse analyseResponse = databaseProject.resolveAnalyseResponse();
        if (analyseResponse != null && analyseResponse.getCode() == 0) {
            List<AlgAnalyseResult> algAnalyseResultList = analyseResponse.getAlgAnalyseResultsList();
            for (AlgAnalyseResult algAnalyseResult : algAnalyseResultList) {
                if (algAnalyseResult.getCode() == 1)
                    continue;
                for (Map.Entry<String, FileAnalyseResults> entry : algAnalyseResult.getFileAnalyseResultsMap()
                        .entrySet()) {
                    FileAnalysis fileAnalysis = files.get(entry.getKey());
                    fileAnalysis.setAnalyseResults(
                            entry.getValue().getAnalyseResultsList().stream().map(r -> new AnalysisResult(r))
                                    .collect(Collectors.toList()));
                }
            }
        }

        /* 写入缓存 */
        redisTemplate.opsForHash().putAll(hashKey, files);
        redisTemplate.expire(hashKey, projectProperties.getExpiration());
        return files;
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
    public FileAnalysisVO readFile(int projectId, String filePath) throws ServiceError {
        Map<String, FileAnalysis> files = fetch(projectId);
        FileAnalysis fileAnalysis = files.get(filePath);
        if (fileAnalysis == null)
            throw new ServiceError(ServiceErrorType.FILE_NOT_FOUND);
        return new FileAnalysisVO(fileAnalysis);
    }

    /**
     * 通过项目id查询项目结构
     * 
     * @param projectId
     * @return 不为{@code null}
     * @throws ServiceError
     * @see FileAnalysisBrief
     * @see DirectoryEntry
     */
    public DirectoryEntry<FileAnalysisBrief> read(int projectId) throws ServiceError {
        Map<String, FileAnalysis> files = fetch(projectId);
        DirectoryEntry<FileAnalysisBrief> directoryEntry = new DirectoryEntry<>();
        for (Map.Entry<String, FileAnalysis> entry : files.entrySet())
            directoryEntry.addFileEntry(entry.getKey(), new FileAnalysisBrief(entry.getValue()));
        return directoryEntry;
    }
}
