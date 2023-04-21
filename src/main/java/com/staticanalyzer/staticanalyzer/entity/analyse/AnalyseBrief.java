package com.staticanalyzer.staticanalyzer.entity.analyse;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@ApiModel(description = "ProjectVO中的分析简报")
public class AnalyseBrief {

    @ApiModelProperty(value = "算法类型如UninitializedVariable", required = true)
    private String analyseType;

    @ApiModelProperty(value = "整个Project中最严重的结果", required = true)
    private AnalyseStatus status;
}
