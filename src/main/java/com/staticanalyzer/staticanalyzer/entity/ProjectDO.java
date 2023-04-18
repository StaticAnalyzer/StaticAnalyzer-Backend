package com.staticanalyzer.staticanalyzer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

@Data
@ApiModel(description = "封装项目数据")
public class ProjectDO {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "项目id", required = false)
    private int id;

    @ApiModelProperty(value = "项目所属用户id", required = false)
    private int userId;

    @ApiModelProperty(value = "源码包(tar.gz)", required = true)
    private byte[] sourceCode;

    @ApiModelProperty(value = "配置文件", required = true)
    private String config;

    @ApiModelProperty(value = "分析结果", required = false)
    private String analyseResult;
}
