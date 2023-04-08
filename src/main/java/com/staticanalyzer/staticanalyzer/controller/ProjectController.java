package com.staticanalyzer.staticanalyzer.controller;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.staticanalyzer.staticanalyzer.entities.Project;
import com.staticanalyzer.staticanalyzer.entities.Result;
import com.staticanalyzer.staticanalyzer.mapper.ProjectMapper;
import com.staticanalyzer.staticanalyzer.service.AlgorithmService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api("项目控制器")
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
    @ApiOperation("文件上传")
    public Result upload(@PathVariable int id, @RequestParam(value = "sourceCode") MultipartFile sourceCode,
                         @RequestParam(value = "config") String config) {
        Project project = new Project();

        try {
            project.setUserId(id);
            project.setSourceCode(sourceCode.getBytes());
            project.setConfig(config);
        } catch (IOException ioe) {
            return new Result(Result.REJECTED, Map.of("msg", "上传失败，文件错误"));
        }

        projectMapper.insert(project);
        taskPool.submit(new Task(project));
        return new Result(Result.ACCEPTED, Map.of("msg", "上传成功，编号" + project.getId()));
    }

    @GetMapping("/user/{id}/project")
    @ApiOperation("查询已上传的任务编号")
    public Result queryAll(@PathVariable int id) {
        List<Integer> dataBaseProjectIdList = projectMapper.selectIdByUserId(id);
        return new Result(Result.ACCEPTED, Map.of("project_id", dataBaseProjectIdList));
    }

    @GetMapping("/user/{id}/project/{projectId}")
    @ApiOperation("查询任务结果")
    public Result query(@PathVariable int id, @PathVariable int projectId) {
        Project dataBaseProject = projectMapper.selectById(projectId);
        if (dataBaseProject == null)
            return new Result(Result.REJECTED, Map.of("msg", "查询失败，任务不存在"));

        if (dataBaseProject.getUserId() != id)
            return new Result(Result.REJECTED, Map.of("msg", "查询失败，用户无权限"));

        return new Result(Result.ACCEPTED, Map.of("project", dataBaseProject));
    }
}
