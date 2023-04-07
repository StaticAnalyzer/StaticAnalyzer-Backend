package com.staticanalyzer.staticanalyzer.entities;

import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotation.IdType;

@ApiModel("项目类")
public class Project {
    @ApiModelProperty("项目编号")
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    @ApiModelProperty("项目所属的用户编号")
    private int userId;

    @ApiModelProperty("项目源码文件压缩包(tar.gz)")
    private byte[] sourceCode;

    @ApiModelProperty("项目配置文件")
    private String config;

    @ApiModelProperty("项目分析结果")
    private String analyseResult;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public byte[] getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(byte[] sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getAnalyseResult() {
        return analyseResult;
    }

    public void setAnalyseResult(String analyseResult) {
        this.analyseResult = analyseResult;
    }
}
