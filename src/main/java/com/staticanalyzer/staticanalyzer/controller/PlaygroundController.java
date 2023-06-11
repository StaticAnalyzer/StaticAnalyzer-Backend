package com.staticanalyzer.staticanalyzer.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.staticanalyzer.staticanalyzer.entity.Result;
import com.staticanalyzer.staticanalyzer.entity.file.SrcFileAnalysis;
import com.staticanalyzer.staticanalyzer.entity.project.SimpleProject;
import com.staticanalyzer.staticanalyzer.service.PlaygroundService;
import com.staticanalyzer.staticanalyzer.service.error.ServiceError;

@RestController
@Api(description = "即时测试")
public class PlaygroundController {

    @Autowired
    private PlaygroundService playgroundService;

    @PostMapping("/playground/test")
    @ApiOperation(value = "上传单个文件")
    public Result<SrcFileAnalysis> upload(@RequestBody SimpleProject simpleProject) {
        try {
            SrcFileAnalysis analysis = playgroundService.testSingle(simpleProject.getCode(),
                    simpleProject.getConfig());
            return Result.ok("测试成功", analysis);
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }

}
