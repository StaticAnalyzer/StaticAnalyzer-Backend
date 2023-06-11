package com.staticanalyzer.staticanalyzer.entity.analysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 算法分析问题报告
 * 
 * @author Yang Yu
 * @since 0.3
 */
@lombok.Getter
@lombok.Setter
@ApiModel(description = "算法分析问题报告")
public class AnalysisProblem {

    @ApiModelProperty(value = "文件名", required = true)
    private String file;

    @ApiModelProperty(value = "问题严重性", required = true)
    private AnalysisStatus severity;

    @ApiModelProperty(value = "问题出现行", required = true)
    private int line;

    @ApiModelProperty(value = "提示信息", required = true)
    private String message;

}
