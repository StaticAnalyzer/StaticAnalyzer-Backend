package com.staticanalyzer.staticanalyzer.entity.analysis;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.staticanalyzer.algservice.AnalyseResultEntry;
import com.staticanalyzer.staticanalyzer.entity.project.FileEntry;

/**
 * 单个文件分析简报
 * 
 * @see com.staticanalyzer.staticanalyzer.entity.project.FileEntry
 */
@Data
@ApiModel(description = "单个文件分析简报")
public class FileAnalysisBrief implements FileEntry {

    /**
     * 文件名
     * 可能作为键值
     */
    @ApiModelProperty(value = "文件名", required = true)
    private String name;

    /**
     * 文件中权值最高的结果
     * 
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisStatus
     */
    @ApiModelProperty(value = "文件中权值最高的结果", required = true)
    private AnalysisStatus severity;

    /**
     * 从文件分析结果生成简报
     * 
     * @param fileAnalysis
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis
     */
    public FileAnalysisBrief(FileAnalysis fileAnalysis) {
        name = fileAnalysis.getName();
        severity = AnalysisStatus.Pass;

        for (AnalyseResultEntry analyseResult : fileAnalysis.getAnalyseResults()) {
            AnalysisStatus currentSeverity = AnalysisStatus.valueOf(analyseResult.getSeverity());
            if (currentSeverity.compareTo(severity) > 0)
                severity = currentSeverity;
        }
    }
}
