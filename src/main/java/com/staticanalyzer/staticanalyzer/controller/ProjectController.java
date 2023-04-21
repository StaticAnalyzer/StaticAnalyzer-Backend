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

import com.staticanalyzer.staticanalyzer.entity.Response;
import com.staticanalyzer.staticanalyzer.entity.project.Project;
import com.staticanalyzer.staticanalyzer.mapper.ProjectMapper;
import com.staticanalyzer.staticanalyzer.service.AlgorithmService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@RestController
@Api(description = "项目控制接口")
public class ProjectController {
    private static Logger logger = LoggerFactory.getLogger(Task.class);
    private static ExecutorService taskPool = Executors.newFixedThreadPool(10);

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private ProjectMapper projectMapper;

    @Getter
    @Setter
    @AllArgsConstructor
    class Task implements Runnable {
        private Project project;

        @Override
        public void run() {
            logger.info("任务" + project.getId() + "启动");
            project.setAnalyseResult(algorithmService.JustReturn(project.getSourceCode(), project.getConfig()));
            projectMapper.updateById(project);
            logger.info("任务" + project.getId() + "完成");
        }
    }

    @PostMapping("/user/{uid}/project")
    @ApiOperation(value = "项目上传")
    public Response<?> upload(
            @PathVariable int uid,
            @RequestParam(value = "sourceCode") MultipartFile sourceCode,
            @RequestParam(value = "config") String config) {
        Project project = new Project();

        try {
            project.setUserId(uid);
            project.setSourceCode(sourceCode.getBytes());
            project.setConfig(config);
        } catch (IOException ioException) {
            return new Response<>(Response.ERROR, "上传失败");
        }

        projectMapper.insert(project);
        taskPool.submit(new Task(project));
        return new Response<>(Response.OK, "项目" + project.getId() + "上传成功");
    }

    @GetMapping("/user/{uid}/project")
    @ApiOperation(value = "获取项目列表")
    public Response<> queryList(@PathVariable int uid) {
        List<Project> databaseProjectList = projectMapper.selectByUserId(uid);
        /* todo */
    }

    @GetMapping("/user/{uid}/project/{pid}")
    @ApiOperation(value = "获取项目目录")
    public Response<> queryDirectory(
            @PathVariable int uid,
            @PathVariable int pid) {
        Project databaseProject = projectMapper.selectById(pid);
        /* */
    }

    @GetMapping("/user/{uid}/project/{pid}/{path}")
    @ApiOperation(value = "获取任务结果")
    public Response<> queryResult(
            @PathVariable int uid,
            @PathVariable int pid,
            @PathVariable String path) {
        Project databaseProject = projectMapper.selectById(pid);
        /* */
    }
}
