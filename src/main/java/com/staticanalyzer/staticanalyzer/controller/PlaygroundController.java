package com.staticanalyzer.staticanalyzer.controller;

import io.swagger.annotations.Api;

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
    public Result<SrcFileAnalysis> playgroundSubmit(@RequestBody SimpleProject requestBody) {
        try {
            SrcFileAnalysis newFileEntryVO = playgroundService.testSingle(requestBody.getCode(),
                    requestBody.getConfig());
            return Result.ok("测试成功", newFileEntryVO);
        } catch (ServiceError serviceError) {
            return Result.error(serviceError.getMessage());
        }
    }
}
