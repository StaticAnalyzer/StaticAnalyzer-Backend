package com.staticanalyzer.staticanalyzer.controller;

import com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisProblem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.staticanalyzer.staticanalyzer.entity.Result;
import com.staticanalyzer.staticanalyzer.entity.file.SrcDirectory;
import com.staticanalyzer.staticanalyzer.entity.file.SrcFileAnalysis;
import com.staticanalyzer.staticanalyzer.entity.project.Project;
import com.staticanalyzer.staticanalyzer.entity.project.ProjectVO;
import com.staticanalyzer.staticanalyzer.service.ProjectService;
import com.staticanalyzer.staticanalyzer.service.error.ServiceError;
import com.staticanalyzer.staticanalyzer.service.error.ServiceErrorType;

@RestController
@Api(description = "项目控制器")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/user/{uid}/project")
    @ApiOperation(value = "项目上传接口")
    public Result<?> upload(
            @PathVariable("uid") int userId,
            @RequestParam(value = "sourceCode") MultipartFile sourceCode,
            @RequestParam(value = "config") String config) {
        try {
            Project project = projectService.create(userId, sourceCode.getBytes(), config);
            projectService.submit(project);
            return Result.ok("上传成功");
        } catch (java.io.IOException ioException) {
            return Result.error(ServiceErrorType.BAD_PROJECT.getMsg());
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }

    @GetMapping("/user/{uid}/project")
    @ApiOperation(value = "项目查询接口")
    public Result<java.util.List<ProjectVO>> read(@PathVariable("uid") int userId) {
        java.util.List<ProjectVO> projectList = projectService.queryProj(userId);
        return Result.ok("查询成功", projectList);
    }

    @GetMapping("/user/{uid}/project/{pid}")
    @ApiOperation(value = "项目目录查询接口")
    public Result<SrcDirectory> read(
            @PathVariable("uid") int userId,
            @PathVariable("pid") int projectId) {
        try {
            SrcDirectory projDirectory = projectService.read(projectId);
            return Result.ok("目录查询成功", projDirectory);
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }

    @GetMapping("/user/{uid}/project/{pid}/file")
    @ApiOperation(value = "文件查询接口")
    public Result<SrcFileAnalysis> read(
            @PathVariable("uid") int userId,
            @PathVariable("pid") int projectId,
            @RequestParam(value = "path") String path) {
        try {
            SrcFileAnalysis analysis = projectService.readFile(projectId, path);
            return Result.ok("文件查询成功", analysis);
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }

    @GetMapping("/user/{uid}/project/{pid}/problem")
    @ApiOperation(value = "问题查询接口")
    public Result<java.util.List<AnalysisProblem>> query(
            @PathVariable("uid") int userId,
            @PathVariable("pid") int projectId){
        try {
            java.util.List<AnalysisProblem> analysisProblems = projectService.queryProblem(projectId);
            return Result.ok("问题查询成功", analysisProblems);
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }

}
