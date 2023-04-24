package com.staticanalyzer.staticanalyzer.controller;

import java.io.IOException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.staticanalyzer.staticanalyzer.entity.Result;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisDe
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisDetail;tailFileAnalysisDetail;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisBrief;
import com.staticanalyzer.staticanalyzer.entity.project.DirectoryEntry;
import com.staticanalyzer.staticanalyzer.entity.project.Project;
import com.staticanalyzer.staticanalyzer.entity.project.ProjectVO;
import com.staticanalyzer.staticanalyzer.service.ProjectService;

/**
 * 项目控制器
 * 定义所有与项目相关的请求操作
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
     * @return data始终置空
     */
    @PostMapping("/user/{uid}/project")
    @ApiOperation(value = "项目上传接口")
    public Result<?> upload(
            @PathVariable("uid") int userId,
            @RequestParam(value = "sourceCode") MultipartFile sourceCode,
            @RequestParam(value = "config") String config) {
        try {
            Project project = projectService.create(userId, sourceCode.getBytes(), config);
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
        List<ProjectVO> projectVOList = projectService.findByUserId(userId);
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
        DirectoryEntry<FileAnalysisBrief> directoryEntryVO = projectService.findByProjectId(userId, projectId);
        return Result.ok("项目目录查询成功", directoryEntryVO);
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
    public Result<FileAnalysisDetail> read(
            @PathVariable("uid") int userId,
            @PathVariable("pid") int projectId,
            @PathVariable String path) {
        FileAnalysisDetail fileEntry = projectService.findByPath(userId, projectId, path);
        return Result.ok("文件查询成功", fileEntry);
    }
}
