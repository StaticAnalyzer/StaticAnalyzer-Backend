package com.staticanalyzer.staticanalyzer.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.staticanalyzer.staticanalyzer.entity.project.Project;
import com.staticanalyzer.staticanalyzer.mapper.ProjectMapper;
import com.staticanalyzer.staticanalyzer.service.ProjectService;

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

    private static Logger logger = LoggerFactory.getLogger(Task.class);
    private static ExecutorService taskPool = Executors.newFixedThreadPool(10);

    @Getter
    @Setter
    @AllArgsConstructor
    class Task implements Runnable {
        private Project project;

        @Override
        public void run() {
            logger.info("任务" + project.getId() + "启动");
            // project.setAnalyseResult(algorithmService.JustReturn(project.getSourceCode(),
            // project.getConfig()));
            projectMapper.updateById(project);
            logger.info("任务" + project.getId() + "完成");
        }
    }

    public Project create(int userId, byte[] sourceCode, String config) {
        Project project = new Project();
        project.setUserId(userId);
        project.setSourceCode(sourceCode);
        project.setConfig(config);

        projectMapper.insert(project);
        taskPool.submit(new Task(project));
        return project;
    }
}
