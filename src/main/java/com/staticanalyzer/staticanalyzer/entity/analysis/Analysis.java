package com.staticanalyzer.staticanalyzer.entity.analysis;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@ApiModel(description = "算法分析结果")
public class Analysis {

    @ApiModelProperty(value = "算法类型", required = true)
    private String analyseType;

    @ApiModelProperty(value = "算法得到的权值最高结果", required = true)
    private AnalysisStatus status;
}
