package com.staticanalyzer.staticanalyzer.service;

import java.io.IOException;
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
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysisVO;
import com.staticanalyzer.staticanalyzer.service.error.ServiceError;
import com.staticanalyzer.staticanalyzer.service.error.ServiceErrorType;
import com.staticanalyzer.staticanalyzer.utils.TarGzFileCreator;

@Service
public class PlaygroundService {

    @Autowired
    private AlgorithmService algorithmService;

    public FileAnalysisVO testSingle(String code, String config) throws ServiceError {
        AnalyseResponse analyseResponse;
        try {
            TarGzFileCreator tarGzFileCreator = new TarGzFileCreator();
            tarGzFileCreator.addFileToTarGz("main.cpp", code);
            analyseResponse = algorithmService.Analyse(tarGzFileCreator.getTarGzBytes(), config);
        } catch (IOException ioException) {
            throw new ServiceError(ServiceErrorType.BAD_PROJECT);
        }

        FileAnalysis newFileEntry = new FileAnalysis();
        newFileEntry.setName("main.cpp");
        newFileEntry.setSrc(code);
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
        return new FileAnalysisVO(newFileEntry);
    }
}
