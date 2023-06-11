package com.staticanalyzer.staticanalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.staticanalyzer.algservice.AnalyseResponse;

import com.staticanalyzer.staticanalyzer.entity.file.SrcFileAnalysis;
import com.staticanalyzer.staticanalyzer.entity.file.SrcFile;
import com.staticanalyzer.staticanalyzer.service.error.ServiceError;
import com.staticanalyzer.staticanalyzer.service.error.ServiceErrorType;
import com.staticanalyzer.staticanalyzer.utils.TarGzUtils;

/**
 * 单文件即时测试服务
 * 
 * @author YangYu
 * @since 0.3
 */
@Service
public class PlaygroundService {

    private static String DEFAULT_SRCDIR = "";
    private static String DEFAULT_SRCFILE = "main.cpp";

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private ProjectService projectService;

    /**
     * 单文件即时测试
     * <p>
     * 该服务不是多线程的
     * </p>
     * 
     * @param code   源代码
     * @param config 配置文件
     * @return {@code analysis} 单文件分析结果
     * @throws ServiceError
     */
    public SrcFileAnalysis testSingle(String code, String config) throws ServiceError {
        AnalyseResponse analyseResponse;
        SrcFile srcFile;
        try {
            srcFile = new SrcFile();
            srcFile.setName(DEFAULT_SRCFILE);
            srcFile.setSrc(code);
            analyseResponse = algorithmService.Analyse(TarGzUtils.compressSingle(srcFile), config);
        } catch (java.io.IOException ioException) {
            throw new ServiceError(ServiceErrorType.BAD_PROJECT);
        }

        SrcFileAnalysis analysis = new SrcFileAnalysis(srcFile);
        java.util.Map<String, SrcFileAnalysis> simpleAnalysis = new java.util.HashMap<>();
        simpleAnalysis.put(DEFAULT_SRCDIR + analysis.getName(), analysis);
        projectService.parseAnalyseResponse(simpleAnalysis, analyseResponse);

        return analysis;
    }

}
