package com.staticanalyzer.staticanalyzer.entity.analyse;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

import com.staticanalyzer.algservice.AnalyseResultEntry;

@Data
@ApiModel(description = "单个文件分析结果")
public class FileEntry {

    @ApiModelProperty(value = "文件名", required = true)
    private String name;

    @ApiModelProperty(value = "源代码", required = true)
    private String src;

    @ApiModelProperty(value = "分析结果列表", required = false)
    private List<AnalyseResultEntry> analyseResults;
}