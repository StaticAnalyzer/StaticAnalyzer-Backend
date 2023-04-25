package com.staticanalyzer.staticanalyzer.entity.analysis;

import java.util.List;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 文件分析报告
 * 
 * @author iu_oi
 * @since 0.0.2
 * @see FileEntry
 */
@Data
@ApiModel(description = "单个文件分析报告")
public class FileAnalysisVO {

    /* 源代码 */
    @ApiModelProperty(value = "源代码", required = true)
    private String src;

    /**
     * 综合所有算法的分析结果列表
     * 用于可视化标注
     * 
     * @see AnalysisResult
     */
    @ApiModelProperty(value = "分析结果列表", required = false)
    private List<AnalysisResult> analyseResults;

    /**
     * 从文件分析结果生成报告
     * 
     * @param fileAnalysis
     * @see FileAnalysis
     * @see AnalysisResult
     */
    public FileAnalysisVO(FileAnalysis fileAnalysis) {
        src = fileAnalysis.getSrc();
        analyseResults = fileAnalysis.getAnalyseResults();
    }
}
