package com.staticanalyzer.staticanalyzer.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.staticanalyzer.staticanalyzer.entities.Project;
import com.staticanalyzer.staticanalyzer.entities.Result;
import com.staticanalyzer.staticanalyzer.entities.ResultBuilder;
import com.staticanalyzer.staticanalyzer.mapper.ProjectMapper;
import com.staticanalyzer.staticanalyzer.service.AlgorithmService;

@RestController
public class ProjectController {
    private static ExecutorService taskPool = Executors.newFixedThreadPool(10);

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private ProjectMapper projectMapper;

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
            project.setAnalyseResult(algorithmService.JustReturn(project.getSourceCode(), project.getConfig()));
            projectMapper.updateById(project);
        }
    }

    @PostMapping("/user/{id}/project")
    public Result upload(@PathVariable int id, @RequestBody MultipartFile sourceCode, @RequestBody String config) {
        Project project = new Project();
        project.setUserId(id);
        try {
            project.setSourceCode(sourceCode.getBytes());
        } catch (IOException ioe) {
            return new ResultBuilder().setCode(Result.FAILURE).build();
        }
        project.setConfig(config);

        projectMapper.insert(project);
        taskPool.submit(new Task(project));
        return new ResultBuilder().setCode(Result.SUCCESS).build();
    }

    @GetMapping("/user/{id}/project")
    public Result queryAll(@PathVariable int id) {
        List<Project> savedProjects = projectMapper.selectByUserId(id);
        return new ResultBuilder().setCode(Result.SUCCESS)
                .addField("projects", savedProjects)
                .build();
    }

    @GetMapping("/user/{id}/project/{projectId}")
    public Result query(@PathVariable int id, @PathVariable int projectId) {
        Project savedProject = projectMapper.selectById(projectId);
        if (savedProject == null || savedProject.getUserId() != id) {
            return new ResultBuilder().setCode(Result.FAILURE).build();
        }

        return new ResultBuilder().setCode(Result.SUCCESS)
                .addField("project", savedProject)
                .build();
    }
}
