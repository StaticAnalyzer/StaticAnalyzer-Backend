package com.staticanalyzer.staticanalyzer.controller;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.staticanalyzer.staticanalyzer.entities.Project;
import com.staticanalyzer.staticanalyzer.mapper.ProjectMapper;
import com.staticanalyzer.staticanalyzer.service.AlgorithmService;

@RestController
public class ProjectController {
    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private ProjectMapper projectMapper;

    private static Logger logger = LoggerFactory.getLogger(ProjectController.class);
    private static ExecutorService taskPool = Executors.newFixedThreadPool(10);

    class Task implements Runnable {
        private Project project;

        public Task(Project project) {
            this.project = project;
        }

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }

        @Override
        public void run() {
            logger.info("Started task " + project.getId());
            project.setAnalyseResult(algorithmService.JustReturn(project.getSourceCode(), project.getConfig()));
            projectMapper.updateById(project);
            logger.info("Task " + project.getId() + " finished");
        }
    }

    @PostMapping("/user/{id}/project")
    public Map<String, Object> upload(@PathVariable int id, @RequestBody MultipartFile sourceCode,
            @RequestBody String config) {
        Project project = new Project();
        try {
            project.setUserId(id);
            project.setSourceCode(sourceCode.getBytes());
            project.setConfig(config);
        } catch (IOException ioe) {
            return Map.of("code", -1, "msg", "文件上传失败");
        }
        projectMapper.insert(project);
        taskPool.submit(new Task(project));
        return Map.of("code", 0, "msg", "文件上传成功，任务编号" + project.getId());
    }

    @GetMapping("/user/{id}/project")
    public Map<String, Object> queryAll(@PathVariable int id) {
        List<Integer> dataBaseProjectIdList = projectMapper.selectIdByUserId(id);
        return Map.of("code", 0, "project_id", dataBaseProjectIdList);
    }

    @GetMapping("/user/{id}/project/{projectId}")
    public Map<String, Object> query(@PathVariable int id, @PathVariable int projectId) {
        Project dataBaseProject = projectMapper.selectById(projectId);
        if (dataBaseProject == null || dataBaseProject.getUserId() != id)
            return Map.of("code", -1, "msg", "查询结果失败，用户无权限或文件不存在");
        return Map.of("code", 0, "project", dataBaseProject);
    }
}
