package com.staticanalyzer.staticanalyzer.entity.analysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 分析问题单元
 * 
 * @author Yang Yu
 * @since 0.3
 */
@lombok.Getter
@lombok.Setter
@ApiModel(description = "分析问题单元")
public class AnalysisProblem extends AnalysisResult {

    @ApiModelProperty(value = "文件名", required = true)
    private String file;

    public AnalysisProblem(String filename, AnalysisResult analysisResult) {
        super(analysisResult);
        this.file = filename;
    }

}
