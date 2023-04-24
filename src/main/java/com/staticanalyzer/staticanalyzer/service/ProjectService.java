package com.staticanalyzer.staticanalyzer.service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.staticanalyzer.algservice.AnalyseResponse;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisBrief;
import com.staticanalyzer.staticanalyzer.entity.project.DirectoryEntry;
import com.staticanalyzer.staticanalyzer.entity.project.Project;
import com.staticanalyzer.staticanalyzer.entity.project.ProjectVO;
import com.staticanalyzer.staticanalyzer.mapper.ProjectMapper;
import com.staticanalyzer.staticanalyzer.service.ProjectService;
import com.staticanalyzer.staticanalyzer.utils.ZipUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Service
public class ProjectService {

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private RedisTemplate<String, List<ProjectVO>> projectVORedisTemplate;

    @Autowired
    private RedisTemplate<String, DirectoryEntry<FileAnalysis>> projectRedisTemplate;

    private static Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);
    private static ExecutorService TASK_POOL = Executors.newFixedThreadPool(10);
    private static String CACHE_KEY_USER = "user:";
    private static String CACHE_KEY_PROJECT = "project:";

    @Getter
    @Setter
    @AllArgsConstructor
    class Task implements Runnable {
        private Project project;

        @Override
        public void run() {
            LOGGER.info("任务" + project.getId() + "启动");
            AnalyseResponse analyseResponse = algorithmService.Analyse(project.getSourceCode(), project.getConfig());
            if (!project.updateAnalyseResult(analyseResponse)) {
                LOGGER.info("任务" + project.getId() + "失败");
            } else {
                projectMapper.updateById(project);
                cleanCached(project);
                fetch(project.getUserId(), project.getId());
                LOGGER.info("任务" + project.getId() + "完成");
            }
        }
    }

    private void cleanCached(Project project) {
        String keyByProjectId = CACHE_KEY_USER + project.getUserId() + CACHE_KEY_PROJECT + project.getId();
        String keyByUserId = CACHE_KEY_USER + project.getUserId() + CACHE_KEY_PROJECT;
        projectRedisTemplate.opsForValue().getAndDelete(keyByProjectId);
        projectRedisTemplate.opsForValue().getAndDelete(keyByUserId);
    }

    private DirectoryEntry<FileAnalysis> fetch(int userId, int projectId) {
        ValueOperations<String, DirectoryEntry<FileAnalysis>> operations = projectRedisTemplate.opsForValue();
        String keyByProjectId = CACHE_KEY_USER + userId + CACHE_KEY_PROJECT + projectId;

        DirectoryEntry<FileAnalysis> cachedAnalysedProject = operations.get(keyByProjectId);
        if (cachedAnalysedProject == null) {
            Project databaseProject = projectMapper.selectById(projectId);
            if (databaseProject == null)
                return null;

            try {
                cachedAnalysedProject = ZipUtils.fromTarGz(databaseProject.getSourceCode());
                DirectoryEntry.analyse(cachedAnalysedProject, databaseProject.resolveAnalyseResponse());
            } catch (IOException ioException) {
                return null;
            }
            operations.set(keyByProjectId, cachedAnalysedProject, 30, TimeUnit.MINUTES);
        }
        return cachedAnalysedProject;
    }

    public Project create(int userId, byte[] sourceCode, String config) {
        Project project = new Project();
        project.setUserId(userId);
        project.setSourceCode(sourceCode);
        project.setConfig(config);

        projectMapper.insert(project);
        TASK_POOL.submit(new Task(project));
        return project;
    }

    public List<ProjectVO> findByUserId(int userId) {
        ValueOperations<String, List<ProjectVO>> operations = projectVORedisTemplate.opsForValue();
        String keyByUserId = CACHE_KEY_USER + userId + CACHE_KEY_PROJECT;

        List<ProjectVO> cachedProjectVOList = operations.get(keyByUserId);
        if (cachedProjectVOList == null) {
            List<Project> databaseProjectList = projectMapper.selectByUserId(userId);
            if (databaseProjectList != null) {
                cachedProjectVOList = new LinkedList<>();
                for (Project project : databaseProjectList)
                    cachedProjectVOList.add(ProjectVO.fromProject(project));
                operations.set(keyByUserId, cachedProjectVOList, 30, TimeUnit.MINUTES);
            }
        }
        return cachedProjectVOList;
    }

    public DirectoryEntry<FileAnalysisBrief> findByProjectId(int userId, int projectId) {
        DirectoryEntry<FileAnalysis> cachedAnalysedProject = fetch(userId, projectId);
        return DirectoryEntry.visualize(cachedAnalysedProject);
    }

    public FileAnalysis findByPath(int userId, int projectId, String path) {
        DirectoryEntry<FileAnalysis> cachedAnalysedProject = fetch(userId, projectId);
        if (cachedAnalysedProject == null)
            return null;
        return cachedAnalysedProject.getFileAt(path);
    }
}
