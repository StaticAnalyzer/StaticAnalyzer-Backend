package com.staticanalyzer.staticanalyzer.entity.analysis;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 单个文件分析报告
 * 
 * @see com.staticanalyzer.staticanalyzer.entity.project.FileEntry
 */
@Data
@ApiModel(description = "单个文件分析报告")
public class FileAnalysisVO {

    /**
     * 源代码
     */
    @ApiModelProperty(value = "源代码", required = true)
    private String src;

    /**
     * 综合所有算法的分析结果列表
     * 用于可视化标注
     * 
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisResult
     */
    @ApiModelProperty(value = "分析结果列表", required = false)
    private List<AnalysisResult> analyseResults;

    /**
     * 从文件分析结果生成报告
     * 
     * @param fileAnalysis
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisResult
     */
    public FileAnalysisVO(FileAnalysis fileAnalysis) {
        src = fileAnalysis.getSrc();
        analyseResults = fileAnalysis.getAnalyseResults();
    }
}
