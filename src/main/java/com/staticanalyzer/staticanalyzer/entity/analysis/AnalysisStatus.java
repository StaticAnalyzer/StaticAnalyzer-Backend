package com.staticanalyzer.staticanalyzer.entity.analysis;

/**
 * 针对分析算法的分析总结
 */
public enum AnalysisStatus {

    /**
     * 结果中存在一个或多个分析失败的文件
     */
    AnalyseError,

    /**
     * 分析通过
     */
    Pass,

    /**
     * 建议
     */
    Hint,

    /**
     * 分析信息
     */
    Info,

    /**
     * 警告
     */
    Warning,

    /**
     * 严重错误
     */
    Error
}
