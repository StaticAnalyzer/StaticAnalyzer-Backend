package com.staticanalyzer.staticanalyzer.entity.project;

/**
 * 项目状态
 */
public enum ProjectStatus {

    /**
     * 分析完成
     * {@code analyseResult}为analyseResponse对象的json表示
     */
    Complete,

    /**
     * 分析未完成
     * {@code analyseResult}为空
     */
    Queueing,

    /**
     * 分析完成，但出现错误
     * {@code analyseResult}为空
     */
    Error
}