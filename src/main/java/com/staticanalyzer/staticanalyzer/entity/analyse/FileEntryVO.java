package com.staticanalyzer.staticanalyzer.entity.analyse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

import com.staticanalyzer.algservice.AnalyseResultEntry;
import com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisStatus;
import com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis;

@Data
@ApiModel(description = "单个文件分析简报")
public class FileEntryVO {

    @ApiModelProperty(value = "文件名", required = true)
    private String name;

    @ApiModelProperty(value = "最高级别分析结果", required = true)
    private AnalysisStatus severity;

    public static FileEntryVO fromFileEntry(FileAnalysis fileEntry) {
        FileEntryVO newFileEntryVO = new FileEntryVO();
        newFileEntryVO.setName(fileEntry.getName());

        AnalysisStatus severity = AnalysisStatus.Pass;
        for (AnalyseResultEntry analyseResult : fileEntry.getAnalyseResults()) {
            AnalysisStatus currentSeverity = AnalysisStatus.valueOf(analyseResult.getSeverity());
            if (currentSeverity.compareTo(severity) > 0)
                severity = currentSeverity;
        }
        newFileEntryVO.setSeverity(severity);
        return newFileEntryVO;
    }
}
