package com.staticanalyzer.staticanalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.staticanalyzer.algservice.AlgAnalyseResult;
import com.staticanalyzer.algservice.AnalyseResponse;
import com.staticanalyzer.algservice.FileAnalyseResults;

import com.staticanalyzer.staticanalyzer.config.project.ProjectProperties;
import com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisProblem;
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
 * <p>
 * 该项目服务使用了线程池技术，提供项目的提交和查询
 * </p>
 * 
 * @author YangYu
 * @since 0.2
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectProperties projectProperties;

    @Autowired
    private AlgorithmService algorithmService;

    @lombok.Setter
    @lombok.Getter
    @lombok.AllArgsConstructor
    class Task implements Runnable {

        private Project project;

        @Override
        public void run() {
            byte[] sourceCode = project.getSourceCode();
            String config = project.getConfig();
            AnalyseResponse analyseResponse = algorithmService.Analyse(sourceCode, config);

            if (project.updateAnalyseResult(analyseResponse)) {
                // 先删缓存再更新
                String listKey = CACHE_KEY_PROJECTVO + project.getUserId();
                redisTemplate.delete(listKey);
                String hashKey = CACHE_KEY_PROJECT + project.getId();
                redisTemplate.delete(hashKey);
                projectMapper.updateById(project);
            }
        }

    }

    private java.util.concurrent.ExecutorService taskPool;

    /**
     * 提交项目到算法后端
     * 
     * @param project
     */
    public void testComplex(Project project) {
        if (taskPool == null)
            taskPool = java.util.concurrent.Executors.newFixedThreadPool(projectProperties.getTaskLimit());
        taskPool.submit(new Task(project));
    }

    @Autowired
    private ProjectMapper projectMapper;

    /**
     * 新建项目
     * <p>
     * {@code id}和{@code timestamp}会被mysql自动设置
     * </p>
     * 
     * @param userId     用户id
     * @param sourceCode 源代码
     * @param config     配置文件
     * @return {@code project} 新建的项目
     * @throws ServiceError
     */
    public Project createProject(int userId, byte[] sourceCode, String config) throws ServiceError {
        if (sourceCode == null || config == null)
            throw new ServiceError(ServiceErrorType.BAD_PROJECT);

        Project project = new Project();
        project.setUserId(userId);
        project.setSourceCode(sourceCode);
        project.setConfig(config);

        // 先删缓存再更新
        String listKey = CACHE_KEY_PROJECTVO + project.getUserId();
        redisTemplate.delete(listKey);
        projectMapper.insert(project);
        return project;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    private static String CACHE_KEY_PROJECTVO = "project_of_user:";

    /**
     * 查询项目列表，通过所有者id
     * 
     * @param userId 所有者id
     * @return {@code projectList}项目列表，如果未查到，该列表长度为0
     * @see ProjectVO
     */
    public java.util.List<ProjectVO> getProjectInfo(int userId) {
        String listKey = CACHE_KEY_PROJECTVO + userId;
        java.util.List<ProjectVO> projectList = redisTemplate.opsForList().range(listKey, 0, -1);
        if (projectList != null && projectList.size() > 0) /* 直接读取缓存 */
            return projectList;

        // 从数据库拉取
        java.util.List<Project> databaseProjectList = projectMapper.selectByUserId(userId);
        projectList = databaseProjectList.stream().map(p -> new ProjectVO(p))
                .collect(java.util.stream.Collectors.toList());

        // 写入缓存
        if (projectList.size() > 0) {
            redisTemplate.opsForList().leftPushAll(listKey, projectList);
            redisTemplate.expire(listKey, projectProperties.getExpiration());
        }
        return projectList;
    }

    private static String CACHE_KEY_PROJECT = "project:";

    /**
     * 按文件集的格式解析以算法名称组织的analyseResponse
     * <p>
     * 如果analyseResponse中存在未知文件路径，抛出异常
     * </p>
     * 
     * @see SrcFileAnalysis
     * @param analyses        文件集
     * @param analyseResponse 分析回应
     * @throws ServiceError
     */
    public void parseAnalyseResponse(java.util.Map<String, SrcFileAnalysis> analyses, AnalyseResponse analyseResponse)
            throws ServiceError {
        // 解析并对analyses中的条目赋值
        if (analyseResponse != null && analyseResponse.getCode() == 0) {
            java.util.List<AlgAnalyseResult> algAnalyseResultList = analyseResponse
                    .getAlgAnalyseResultsList();
            // 遍历每种算法的结果
            for (AlgAnalyseResult algAnalyseResult : algAnalyseResultList) {
                if (algAnalyseResult.getCode() != 0)
                    continue;

                // 每种算法中都有一个针对单文件的列表，文件以Map的形式组织
                for (java.util.Map.Entry<String, FileAnalyseResults> entry : algAnalyseResult
                        .getFileAnalyseResultsMap().entrySet()) {
                    // 附加该算法针对该文件的结果
                    SrcFileAnalysis analysis = analyses.get(entry.getKey());
                    if (analysis == null) {
                        throw new ServiceError(ServiceErrorType.FILE_NOT_FOUND);
                    }
                    java.util.List<AnalysisResult> analysisResults = entry.getValue().getAnalyseResultsList()
                            .stream().map(res -> new AnalysisResult(algAnalyseResult.getAnalyseType(), res))
                            .collect(java.util.stream.Collectors.toList());
                    if (analysisResults.size() > 0)
                        analysis.getAnalyseResults().addAll(analysisResults);
                }
            }
        }
    }

    /**
     * 获取源文件集
     * <p>
     * 如果分析未完成，则不为{@code SrcFileAnalysis}设置分析数据
     * </p>
     * 
     * @see SrcFileAnalysis
     * @param projectId 项目id
     * @return {@code fileAnalyses}源文件集，如果未查到，该映射大小为0
     * @throws ServiceError
     */
    public java.util.Map<String, SrcFileAnalysis> fetchFromCache(int projectId) throws ServiceError {
        String hashKey = CACHE_KEY_PROJECT + projectId;
        java.util.Map<String, SrcFileAnalysis> analyses = redisTemplate.opsForHash().entries(hashKey);
        if (analyses != null && analyses.size() > 0) // 直接读取缓存
            return analyses;

        // 从数据库中拉取
        Project databaseProject = projectMapper.selectById(projectId);
        if (databaseProject == null)
            throw new ServiceError(ServiceErrorType.PROJECT_NOT_FOUND);

        java.util.Map<String, SrcFile> files;
        try {
            files = TarGzUtils.decompress(databaseProject.getSourceCode());
        } catch (java.io.IOException ioException) {
            throw new ServiceError(ServiceErrorType.BAD_PROJECT);
        }

        for (java.util.Map.Entry<String, SrcFile> entry : files.entrySet())
            analyses.put(entry.getKey(), new SrcFileAnalysis(entry.getValue()));

        AnalyseResponse analyseResponse = databaseProject.resolveAnalyseResponse();
        parseAnalyseResponse(analyses, analyseResponse);

        // 写入缓存
        if (analyses.size() > 0) {
            redisTemplate.opsForHash().putAll(hashKey, analyses);
            redisTemplate.expire(hashKey, projectProperties.getExpiration());
        }
        return analyses;
    }

    /**
     * 通过项目id和文件路径查询文件分析信息
     * 
     * @see SrcFileAnalysis
     * @param projectId 项目id
     * @param filePath  文件路径
     * @return {@code files}源文件分析信息
     * @throws ServiceError
     */
    public SrcFileAnalysis getFileInfo(int projectId, String filePath) throws ServiceError {
        java.util.Map<String, SrcFileAnalysis> files = fetchFromCache(projectId);
        SrcFileAnalysis analysis = files.get(filePath);
        if (analysis == null)
            throw new ServiceError(ServiceErrorType.FILE_NOT_FOUND);
        return analysis;
    }

    /**
     * 通过项目id查询项目结构
     * 
     * @see SrcFileDigest
     * @see SrcDirectory
     * @param projectId 项目id
     * @return {@code directory}源文件结构化分析信息
     * @throws ServiceError
     */
    public SrcDirectory getAllInfo(int projectId) throws ServiceError {
        java.util.Map<String, SrcFileAnalysis> files = fetchFromCache(projectId);
        SrcDirectory directory = new SrcDirectory();
        for (java.util.Map.Entry<String, SrcFileAnalysis> entry : files.entrySet()) {
            SrcFileDigest digest = new SrcFileDigest(entry.getValue());
            directory.addSrcFile(entry.getKey(), digest);
        }
        return directory;
    }

    /**
     * 通过项目id查询项目问题列表
     * 
     * @param projectId 项目id
     * @return {@code problems} 项目问题列表
     * @throws ServiceError
     */
    public java.util.List<AnalysisProblem> getProblems(int projectId) throws ServiceError {
        java.util.Map<String, SrcFileAnalysis> files = fetchFromCache(projectId);
        java.util.List<AnalysisProblem> problems = new java.util.LinkedList<>();

        for (java.util.Map.Entry<String, SrcFileAnalysis> entry : files.entrySet()) {
            SrcFileAnalysis analysis = entry.getValue();
            for (AnalysisResult result : analysis.getAnalyseResults()) {
                AnalysisProblem problem = new AnalysisProblem(entry.getKey(), result);
                problems.add(problem);
            }
        }

        return problems;
    }

}
