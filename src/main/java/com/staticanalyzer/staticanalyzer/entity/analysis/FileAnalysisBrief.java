package com.staticanalyzer.staticanalyzer.entity.analysis;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.staticanalyzer.staticanalyzer.entity.project.FileEntry;

/**
 * 文件分析简报
 * 
 * @author iu_oi
 * @since 0.0.2
 * @see com.staticanalyzer.staticanalyzer.entity.project.FileEntry
 */
@Data
@ApiModel(description = "文件分析简报")
public class FileAnalysisBrief implements FileEntry {

    /* 文件名 */
    @ApiModelProperty(value = "文件名", required = true)
    private String name;

    /**
     * 文件中权值最高的评估
     * 
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisStatus
     */
    @ApiModelProperty(value = "文件中权值最高的评估", example = "Pass", required = true)
    private AnalysisStatus severity;

    /**
     * 从文件分析结果生成简报
     * 
     * @param fileAnalysis
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisResult
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisStatus
     */
    public FileAnalysisBrief(FileAnalysis fileAnalysis) {
        name = fileAnalysis.getName();
        severity = AnalysisStatus.Pass;

        for (AnalysisResult analyseResult : fileAnalysis.getAnalyseResults()) {
            AnalysisStatus currentSeverity = analyseResult.getSeverity();
            if (currentSeverity.compareTo(severity) > 0)
                severity = currentSeverity;
        }
    }
}
