package com.staticanalyzer.staticanalyzer.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.staticanalyzer.staticanalyzer.entities.Project;
import com.staticanalyzer.staticanalyzer.entities.Result;
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
    public Result upload(@PathVariable int id,
            @RequestBody MultipartFile sourceCode,
            @RequestBody String config) {
        Project project = new Project();
        project.setUserId(id);
        try {
            project.setSourceCode(sourceCode.getBytes());
        } catch (IOException ioe) {
            return Result.failure().setField("message", "bad input");
        }
        project.setConfig(config);
        projectMapper.insert(project);
        taskPool.submit(new Task(project));
        return Result.success().setField("message", "project uploaded");
    }

    @GetMapping("/user/{id}/project")
    public Result queryStatus(@PathVariable int id) {
        List<Project> savedProjects = projectMapper.selectByUserId(id);
        Result result = Result.success();
        for (Project project : savedProjects) {
            String msg = String.valueOf(project.getId());
            if (project.getAnalyseResult() != null)
                result.setField(msg, Project.FINISHED);
            else
                result.setField(msg, Project.WAITING);
        }
        return result;
    }

    @GetMapping("/user/{id}/project/{projId}")
    public Result getResult(@PathVariable int id, @PathVariable int projId) {
        Project savedProject = projectMapper.selectById(projId);

        if (savedProject == null || savedProject.getUserId() != id)
            return Result.failure().setField("message", "query failure");

        return Result.success().setField(String.valueOf(savedProject.getId()), savedProject.getAnalyseResult());
    }
}
