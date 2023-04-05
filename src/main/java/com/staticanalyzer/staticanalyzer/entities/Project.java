package com.staticanalyzer.staticanalyzer.entities;

import com.baomidou.mybatisplus.annotation.*;

import javax.validation.constraints.NotNull;

public class Project {
    public static int FINISHED = 1;
    public static int WAITING = 2;

    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    @NotNull
    private int userId;

    @NotNull
    private byte[] sourceCode;

    @NotNull
    private String config;

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
