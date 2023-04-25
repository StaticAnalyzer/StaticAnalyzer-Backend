package com.staticanalyzer.staticanalyzer.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisBrief;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisVO;
import com.staticanalyzer.staticanalyzer.entity.project.DirectoryEntry;
import com.staticanalyzer.staticanalyzer.entity.project.Project;
import com.staticanalyzer.staticanalyzer.entity.project.ProjectVO;
import com.staticanalyzer.staticanalyzer.mapper.ProjectMapper;
import com.staticanalyzer.staticanalyzer.service.ProjectService;
import com.staticanalyzer.staticanalyzer.utils.TarGzUtils;

/**
 * 项目服务
 * 上传和查询分析结果等
 * 
 * @author iu_oi
 * @verion 0.0.1
 */
@Service
public class ProjectService {

    /**
     * grpc算法服务
     */
    @Autowired
    private AlgorithmService algorithmService;

    /**
     * 任务内部类
     * 递交任务的抽象
     */
    @Getter
    @Setter
    @AllArgsConstructor
    class Task implements Runnable {

        /**
         * 需要处理的数据
         */
        private Project project;

        @Override
        public void run() {
            byte[] sourceCode = project.getSourceCode();
            String config = project.getConfig();
            AnalyseResponse analyseResponse = algorithmService.Analyse(sourceCode, config);

            if (project.updateAnalyseResult(analyseResponse)) {
                projectMapper.updateById(project);
                // clean(project);
            }
        }
    }

    /**
     * 线程池
     * 最大{@static 10}
     * 超过则等待
     */
    private static ExecutorService TASK_POOL = Executors.newFixedThreadPool(10);

    /**
     * 递交项目给算法后端
     * 
     * @param project
     */
    public void submit(Project project) {
        TASK_POOL.submit(new Task(project));
    }

    /**
     * project数据库映射
     */
    @Autowired
    private ProjectMapper projectMapper;

    /**
     * 新建项目
     * 同时递交任务给grpc后端
     * 
     * @apiNote 项目id和上传时间戳会被mysql自动设置
     * @param userId
     * @param sourceCode
     * @param config
     * @return 新建的项目
     */
    public Project create(int userId, byte[] sourceCode, String config) {
        Project project = new Project();
        project.setUserId(userId);
        project.setSourceCode(sourceCode);
        project.setConfig(config);

        projectMapper.insert(project);
        return project;
    }

    /**
     * 通过所有者id查询项目
     * 
     * @param userId
     * @return 项目列表，如果没有项目返回空列表
     * @see com.staticanalyzer.staticanalyzer.entity.project.ProjectVO
     */
    public List<ProjectVO> readAll(int userId) {
        List<Project> databaseProjectList = projectMapper.selectByUserId(userId);
        return databaseProjectList.stream().map(p -> new ProjectVO(p)).collect(Collectors.toList());
    }

    /**
     * 项目模板
     */
    @Autowired
    private RedisTemplate projectTemplate;

    /**
     * 项目缓存键值前缀
     */
    private static String CACHE_KEY_PROJECT = "project:";

    /**
     * 获取项目文件集
     * 
     * @apiNote 如果分析未完成，则不为FileAnalysis设置分析数据
     * @param project
     * @return 文件集，如果解压失败返回{@code null}
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis
     */
    private Map<String, FileAnalysis> fetch(int projectId) {
        String hashKey = CACHE_KEY_PROJECT + projectId;
        Map<String, FileAnalysis> files = projectTemplate.opsForHash().entries(hashKey);
        if (files != null)
            return files;

        /* 从数据库中拉取 */
        Project databaseProject = projectMapper.selectById(projectId);
        try {
            files = TarGzUtils.decompress(databaseProject.getSourceCode());
        } catch (IOException ioException) {
            return null;
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
                    fileAnalysis.setAnalyseResults(entry.getValue().getAnalyseResultsList());
                }
            }
        }

        /* 写入缓存 */
        projectTemplate.opsForHash().putAll(hashKey, files);
        projectTemplate.expire(hashKey, 30, TimeUnit.MINUTES);
        return files;
    }

    /**
     * 通过项目id和文件路径查询文件分析信息
     * 
     * @param userId
     * @param projectId
     * @return 查询到的文件分析信息，如果找不到返回{@code null}
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisVO
     */
    public FileAnalysisVO readFile(int projectId, String filePath) {
        Map<String, FileAnalysis> files = fetch(projectId);
        if (files == null)
            return null;

        FileAnalysis fileAnalysis = files.get(filePath);
        if (fileAnalysis == null)
            return null;
        return new FileAnalysisVO(fileAnalysis);
    }

    /**
     * 通过项目id查询项目结构
     * 
     * @param projectId
     * @return 查询到的项目树状结构，如果找不到返回{@code null}
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisBrief
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.DirectoryEntry
     */
    public DirectoryEntry<FileAnalysisBrief> read(int projectId) {
        Map<String, FileAnalysis> files = fetch(projectId);
        if (files == null)
            return null;

        DirectoryEntry<FileAnalysisBrief> directoryEntry = new DirectoryEntry<>();
        for (FileAnalysis fileAnalysis : files.values())
            directoryEntry.addFileEntry(new FileAnalysisBrief(fileAnalysis));
        return directoryEntry;
    }
}
