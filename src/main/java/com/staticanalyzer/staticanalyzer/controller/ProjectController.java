package com.staticanalyzer.staticanalyzer.controller;

import java.io.IOException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.staticanalyzer.staticanalyzer.entity.Response;
import com.staticanalyzer.staticanalyzer.entity.analyse.DirectoryEntry;
import com.staticanalyzer.staticanalyzer.entity.analyse.FileEntryVO;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis;
import com.staticanalyzer.staticanalyzer.entity.project.Project;
import com.staticanalyzer.staticanalyzer.entity.project.ProjectVO;
import com.staticanalyzer.staticanalyzer.service.ProjectService;

@RestController
@Api(description = "项目控制接口")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/user/{uid}/project")
    @ApiOperation(value = "项目上传")
    public Response<?> upload(
            @PathVariable("uid") int userId,
            @RequestParam(value = "sourceCode") MultipartFile sourceCode,
            @RequestParam(value = "config") String config) {
        try {
            Project project = projectService.create(userId, sourceCode.getBytes(), config);
            return new Response<>(Response.OK, "项目" + project.getId() + "上传成功");
        } catch (IOException ioException) {
            return new Response<>(Response.ERROR, "上传失败");
        }
    }

    @GetMapping("/user/{uid}/project")
    @ApiOperation(value = "获取项目列表")
    public Response<List<ProjectVO>> query(@PathVariable("uid") int userId) {
        List<ProjectVO> projectVOList = projectService.findByUserId(userId);
        return new Response<>(Response.OK, "获取项目列表成功", projectVOList);
    }

    @GetMapping("/user/{uid}/project/{pid}")
    @ApiOperation(value = "获取项目目录")
    public Response<DirectoryEntry<FileEntryVO>> query(
            @PathVariable("uid") int userId,
            @PathVariable("pid") int projectId) {
        DirectoryEntry<FileEntryVO> directoryEntryVO = projectService.findByProjectId(userId, projectId);
        return new Response<>(Response.OK, "获取项目目录成功", directoryEntryVO);
    }

    @GetMapping("/user/{uid}/project/{pid}/{path:.+}")
    @ApiOperation(value = "获取任务结果")
    public Response<FileAnalysis> query(
            @PathVariable("uid") int userId,
            @PathVariable("pid") int projectId,
            @PathVariable String path) {
        FileAnalysis fileEntry = projectService.findByPath(userId, projectId, path);
        return new Response<>(Response.OK, "获取任务结果成功", fileEntry);
    }
}
