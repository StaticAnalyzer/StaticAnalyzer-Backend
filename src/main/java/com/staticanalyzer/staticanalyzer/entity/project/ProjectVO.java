package com.staticanalyzer.staticanalyzer.entity.project;

import java.util.List;
import java.util.Date;

import lombok.Data;

import com.staticanalyzer.staticanalyzer.entity.analyse.AnalyseBrief;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

enum ProjectStatus {
    Complete,
    Queueing,
    Error
}

@Data
@ApiModel(description = "项目分析简报")
public class ProjectVO {

    @ApiModelProperty(value = "项目id", required = true)
    private int id;

    @ApiModelProperty(value = "项目上传时间", required = true)
    private Date timestamp;

    @ApiModelProperty(value = "项目分析状态", required = true)
    private ProjectStatus status;

    @ApiModelProperty(value = "项目配置", required = true)
    private String config;

    @ApiModelProperty(value = "分析结果简报列表", required = false)
    private List<AnalyseBrief> analyseBrief;
}
