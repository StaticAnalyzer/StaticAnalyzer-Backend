package com.staticanalyzer.staticanalyzer.entity.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisResult;

/**
 * 文件分析结果
 * <p>
 * {@code analyseResults}默认为{@code null}
 * </p>
 * 
 * @see AnalysisResult
 * @author YangYu
 * @since 0.2
 */
@lombok.Setter
@lombok.Getter
@ApiModel(description = "文件分析结果")
public class SrcFileAnalysis extends SrcFile {

    @ApiModelProperty(value = "分析结果列表", required = false)
    private java.util.List<AnalysisResult> analyseResults;

    public SrcFileAnalysis() {
        this.analyseResults = new java.util.LinkedList<>();
    }

    public SrcFileAnalysis(SrcFile srcFile) {
        this.name = srcFile.name;
        this.src = srcFile.src;
        this.analyseResults = new java.util.LinkedList<>();
    }

}
