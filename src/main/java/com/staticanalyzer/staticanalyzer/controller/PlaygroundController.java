package com.staticanalyzer.staticanalyzer.controller;

import com.staticanalyzer.algservice.AnalyseResponse;
import com.staticanalyzer.staticanalyzer.entity.Result;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisVO;
import com.staticanalyzer.staticanalyzer.entity.playground.PlaygroundRequestParam;
import com.staticanalyzer.staticanalyzer.service.AlgorithmService;
import com.staticanalyzer.staticanalyzer.utils.TarGzFileCreator;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Api(description = "即时测试")
public class PlaygroundController {
    @Autowired
    private AlgorithmService algorithmService;

    @PostMapping("/playground/test")
    public Result<FileAnalysisVO> playgroundSubmit(@RequestBody PlaygroundRequestParam requestBody) throws IOException {
        TarGzFileCreator tarGzFileCreator = new TarGzFileCreator();
        tarGzFileCreator.addFileToTarGz("main.cpp", requestBody.getCode());
        AnalyseResponse analyseResponse = algorithmService.Analyse(tarGzFileCreator.getTarGzBytes(), requestBody.getConfig());
        return null;
    }

}
