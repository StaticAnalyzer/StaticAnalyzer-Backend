package com.staticanalyzer.staticanalyzer.entity.analysis;

import java.util.Arrays;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "详细描述源文件分析结果")
public class AnalyseData {

    @ApiModelProperty(value = "源代码", required = true)
    private byte src[];

    @ApiModelProperty(value = "分析结果列表", required = false)
    private AnalyseResult analyseResults[];

    public addAnalyseResult(AnalyseResult analyseResult) {
        // 性能？？
    }
}
