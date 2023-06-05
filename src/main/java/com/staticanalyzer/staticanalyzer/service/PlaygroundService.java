package com.staticanalyzer.staticanalyzer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

@Service
public class PlaygroundService {

    @Autowired
    private AlgorithmService algorithmService;

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

        SrcFileAnalysis newFileEntry = new SrcFileAnalysis(srcFile);
        newFileEntry.setAnalyseResults(new ArrayList<>());

        /* 设置结果 */
        if (analyseResponse != null && analyseResponse.getCode() == 0) {
            List<AlgAnalyseResult> algAnalyseResultList = analyseResponse.getAlgAnalyseResultsList();
            for (AlgAnalyseResult algAnalyseResult : algAnalyseResultList) {
                if (algAnalyseResult.getCode() != 0)
                    continue;
                Map<String, FileAnalyseResults> fileAnalyseResults = algAnalyseResult.getFileAnalyseResultsMap();
                if (!fileAnalyseResults.containsKey("main.cpp"))
                    continue;
                List<AnalysisResult> newAnalysisResultList = fileAnalyseResults.get("main.cpp").getAnalyseResultsList()
                        .stream().map(r -> new AnalysisResult(r))
                        .collect(Collectors.toList());
                if (newAnalysisResultList.size() > 0) {
                    newFileEntry.getAnalyseResults().addAll(newAnalysisResultList);
                }

            }
        }
        return newFileEntry;
    }
}
