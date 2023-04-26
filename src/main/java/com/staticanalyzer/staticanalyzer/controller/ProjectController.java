package com.staticanalyzer.staticanalyzer.controller;

import java.io.IOException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.staticanalyzer.staticanalyzer.entity.Result;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisBrief;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisVO;
import com.staticanalyzer.staticanalyzer.entity.project.DirectoryEntry;
import com.staticanalyzer.staticanalyzer.entity.project.Project;
import com.staticanalyzer.staticanalyzer.entity.project.ProjectVO;
import com.staticanalyzer.staticanalyzer.service.ProjectService;
import com.staticanalyzer.staticanalyzer.service.error.ServiceError;
import com.staticanalyzer.staticanalyzer.service.error.ServiceErrorType;

/**
 * 定义所有与项目相关的请求操作
 * 
 * @author iu_oi
 * @since 0.0.1
 */
@RestController
@Api(description = "项目控制器")
public class ProjectController {

    @Autowired /* 项目服务 */
    private ProjectService projectService;

    /**
     * 项目上传接口
     * 
     * @apiNote 文件以xxx-form-data方式上传
     * @param userId
     * @param sourceCode
     * @param config
     * @return {@code data = null}
     */
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
        } catch (IOException ioException) {
            return Result.error(ServiceErrorType.BAD_PROJECT.getMsg());
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }

    /**
     * 项目查询接口
     * 查询当前用户下所有项目的状态
     * 
     * @param userId
     * @return 出错时{@code data = null}
     * @see ProjectVO
     */
    @GetMapping("/user/{uid}/project")
    @ApiOperation(value = "项目查询接口")
    public Result<List<ProjectVO>> read(@PathVariable("uid") int userId) {
        List<ProjectVO> projectVOList = projectService.readAll(userId);
        return Result.ok("查询成功", projectVOList);
    }

    /**
     * 项目目录查询接口
     * 查询当前用户下某一项目的目录结构
     * 
     * @param userId
     * @param projectId
     * @return 出错时{@code data = null}
     * @see DirectoryEntry
     * @see FileAnalysisBrief
     */
    @GetMapping("/user/{uid}/project/{pid}")
    @ApiOperation(value = "项目目录查询接口")
    public Result<DirectoryEntry<FileAnalysisBrief>> read(
            @PathVariable("uid") int userId,
            @PathVariable("pid") int projectId) {
        try {
            DirectoryEntry<FileAnalysisBrief> directoryEntry = projectService.read(projectId);
            return Result.ok("目录查询成功", directoryEntry);
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }

    /**
     * 文件查询接口
     * 查询当前用户下某一项目中某一文件的分析结果
     * 
     * @param userId
     * @param projectId
     * @param path
     * @return 出错时{@code data = null}
     * @see FileAnalysisVO
     */
    @GetMapping("/user/{uid}/project/{pid}/file")
    @ApiOperation(value = "文件查询接口")
    public Result<FileAnalysisVO> read(
            @PathVariable("uid") int userId,
            @PathVariable("pid") int projectId,
            @RequestParam(value = "path") String path) {
        try {
            FileAnalysisVO fileEntry = projectService.readFile(projectId, path);
            return Result.ok("文件查询成功", fileEntry);
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }
}
