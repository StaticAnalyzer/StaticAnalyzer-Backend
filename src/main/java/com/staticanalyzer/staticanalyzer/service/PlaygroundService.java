package com.staticanalyzer.staticanalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.staticanalyzer.algservice.AlgAnalyseResult;
import com.staticanalyzer.algservice.AnalyseResponse;
import com.staticanalyzer.algservice.FileAnalyseResults;

import com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisResult;
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

    @Autowired
    private AlgorithmService algorithmService;

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
            srcFile.setName("main.cpp");
            srcFile.setSrc(code);
            analyseResponse = algorithmService.Analyse(TarGzUtils.compressSingle(srcFile), config);
        } catch (java.io.IOException ioException) {
            throw new ServiceError(ServiceErrorType.BAD_PROJECT);
        }

        SrcFileAnalysis analysis = new SrcFileAnalysis(srcFile);

        // 设置特殊结果：main.cpp
        if (analyseResponse != null && analyseResponse.getCode() == 0) {
            java.util.List<AlgAnalyseResult> algAnalyseResultList = analyseResponse.getAlgAnalyseResultsList();
            for (AlgAnalyseResult algAnalyseResult : algAnalyseResultList) {
                if (algAnalyseResult.getCode() != 0)
                    continue;

                java.util.Map<String, FileAnalyseResults> fileAnalyseResults = algAnalyseResult
                        .getFileAnalyseResultsMap();
                if (!fileAnalyseResults.containsKey("main.cpp"))
                    continue;

                java.util.List<AnalysisResult> newAnalysisResultList = fileAnalyseResults.get("main.cpp")
                        .getAnalyseResultsList()
                        .stream().map(r -> new AnalysisResult(r))
                        .collect(java.util.stream.Collectors.toList());
                if (newAnalysisResultList.size() > 0) {
                    analysis.getAnalyseResults().addAll(newAnalysisResultList);
                }

            }
        }

        return analysis;
    }

}
