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
import com.staticanalyzer.staticanalyzer.config.UserConfig;
import com.staticanalyzer.staticanalyzer.entity.analyse.DirectoryEntry;
import com.staticanalyzer.staticanalyzer.entity.analyse.FileEntry;
import com.staticanalyzer.staticanalyzer.entity.analyse.FileEntryVO;
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
    private UserConfig userConfig;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private RedisTemplate<String, List<ProjectVO>> projectVORedisTemplate;

    @Autowired
    private RedisTemplate<String, DirectoryEntry<FileEntry>> projectRedisTemplate;

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
            try {
                project.receiveAnalyseResponse(analyseResponse);
                projectMapper.updateById(project);
                cleanCached(project);
                fetch(project.getUserId(), project.getId());
                LOGGER.info("任务" + project.getId() + "完成");
            } catch (IOException ioException) {
                LOGGER.info("任务" + project.getId() + "失败");
            }
        }
    }

    private void cleanCached(Project project) {
        String keyByProjectId = CACHE_KEY_USER + project.getUserId() + CACHE_KEY_PROJECT + project.getId();
        String keyByUserId = CACHE_KEY_USER + project.getUserId() + CACHE_KEY_PROJECT;
        projectRedisTemplate.opsForValue().getAndDelete(keyByProjectId);
        projectRedisTemplate.opsForValue().getAndDelete(keyByUserId);
    }

    private DirectoryEntry<FileEntry> fetch(int userId, int projectId) {
        ValueOperations<String, DirectoryEntry<FileEntry>> operations = projectRedisTemplate.opsForValue();
        String keyByProjectId = CACHE_KEY_USER + userId + CACHE_KEY_PROJECT + projectId;

        DirectoryEntry<FileEntry> cachedAnalysedProject = operations.get(keyByProjectId);
        if (cachedAnalysedProject == null) {
            Project databaseProject = projectMapper.selectById(projectId);
            if (databaseProject == null)
                return null;

            try {
                cachedAnalysedProject = ZipUtils.fromTarGz(databaseProject.getSourceCode());
                DirectoryEntry.analyse(cachedAnalysedProject, databaseProject.parseAnalyseResponse());
            } catch (IOException ioException) {
                return null;
            }
            operations.set(keyByProjectId, cachedAnalysedProject, userConfig.getExpiration(), TimeUnit.MILLISECONDS);
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
                operations.set(keyByUserId, cachedProjectVOList, userConfig.getExpiration(), TimeUnit.MILLISECONDS);
            }
        }
        return cachedProjectVOList;
    }

    public DirectoryEntry<FileEntryVO> findByProjectId(int userId, int projectId) {
        DirectoryEntry<FileEntry> cachedAnalysedProject = fetch(userId, projectId);
        return DirectoryEntry.visualize(cachedAnalysedProject);
    }

    public FileEntry findByPath(int userId, int projectId, String path) {
        DirectoryEntry<FileEntry> cachedAnalysedProject = fetch(userId, projectId);
        if (cachedAnalysedProject == null)
            return null;
        return cachedAnalysedProject.getFileAt(path);
    }
}
