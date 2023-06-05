package com.staticanalyzer.staticanalyzer.entity.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisResult;

/**
 * 文件分析结果
 * 
 * @author iu_oi
 * @since 0.0.2
 */
@lombok.Setter
@lombok.Getter
@ApiModel(description = "文件分析结果")
public class SrcFileAnalysis extends SrcFile {

    /**
     * 综合所有算法的分析结果列表
     * 
     * @see AnalysisResult
     */
    @ApiModelProperty(value = "分析结果列表", required = false)
    private java.util.List<AnalysisResult> analyseResults;

    public SrcFileAnalysis(SrcFile srcFile) {
        this.name = srcFile.name;
        this.src = srcFile.src;
        this.analyseResults = new java.util.LinkedList<>();
    }

}
