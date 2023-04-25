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

/**
 * 项目控制器
 * 定义所有与项目相关的请求操作
 * 
 * @author iu_oi
 * @version 0.0.1
 */
@RestController
@Api(description = "项目控制器")
public class ProjectController {

    /**
     * 项目服务
     */
    @Autowired
    private ProjectService projectService;

    /**
     * 项目上传接口
     * 
     * @apiNote 文件以xxx-form-data方式上传
     * @param userId
     * @param sourceCode
     * @param config
     * @return data始终为{@code null}
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
            String msg = "项目" + project.getId() + "上传成功";
            return Result.ok(msg);
        } catch (IOException ioException) {
            return Result.ok("上传失败");
        }
    }

    /**
     * 项目查询接口
     * 查询当前用户下所有项目的状态
     * 
     * @param userId
     * @return 项目信息组成的列表
     * @see com.staticanalyzer.staticanalyzer.entity.project.ProjectVO
     */
    @GetMapping("/user/{uid}/project")
    @ApiOperation(value = "项目查询接口")
    public Result<List<ProjectVO>> read(@PathVariable("uid") int userId) {
        List<ProjectVO> projectVOList = projectService.readAll(userId);
        if (projectVOList == null)
            return Result.error("项目查询失败");
        return Result.ok("项目查询成功", projectVOList);
    }

    /**
     * 项目目录查询接口
     * 查询当前用户下某一项目的目录结构
     * 
     * @param userId
     * @param projectId
     * @return 项目文件树结构
     * @see com.staticanalyzer.staticanalyzer.entity.project.DirectoryEntry
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisBrief
     */
    @GetMapping("/user/{uid}/project/{pid}")
    @ApiOperation(value = "项目目录查询接口")
    public Result<DirectoryEntry<FileAnalysisBrief>> read(
            @PathVariable("uid") int userId,
            @PathVariable("pid") int projectId) {
        DirectoryEntry<FileAnalysisBrief> directoryEntry = projectService.read(projectId);
        if (directoryEntry == null)
            return Result.error("项目目录查询失败");
        return Result.ok("项目目录查询成功", directoryEntry);
    }

    /**
     * 文件查询接口
     * 查询当前用户下某一项目中某一文件的分析结果
     * 
     * @param userId
     * @param projectId
     * @param path
     * @return 包括源代码和分析结果的清单
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisDetail
     */
    @GetMapping("/user/{uid}/project/{pid}/{path:.+}")
    @ApiOperation(value = "文件查询接口")
    public Result<FileAnalysisVO> read(
            @PathVariable("uid") int userId,
            @PathVariable("pid") int projectId,
            @PathVariable String path) {
        FileAnalysisVO fileEntry = projectService.readFile(projectId, path);
        if (fileEntry == null)
            return Result.error("文件查询失败");
        return Result.ok("文件查询成功", fileEntry);
    }
}
