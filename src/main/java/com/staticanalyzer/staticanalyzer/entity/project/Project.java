package com.staticanalyzer.staticanalyzer.entity.project;

import java.io.IOException;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import com.google.protobuf.util.JsonFormat;

import com.staticanalyzer.algservice.AnalyseResponse;

@Data
@ApiModel(description = "封装项目数据")
public class Project {

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

    public void receiveAnalyseResponse(AnalyseResponse analyseResponse) throws IOException {
        analyseResult = JsonFormat.printer().includingDefaultValueFields().print(analyseResponse);
    }

    public AnalyseResponse parseAnalyseResponse() throws IOException {
        AnalyseResponse.Builder builder = AnalyseResponse.newBuilder();
        JsonFormat.parser().merge(analyseResult, builder);
        return builder.build();
    }
}