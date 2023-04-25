package com.staticanalyzer.staticanalyzer.entity.analysis;

import io.swagger.annotations.ApiModel;

/**
 * 分析评估
 * 
 * @author iu_oi
 * @since 0.0.2
 */
@ApiModel(description = "分析评估")
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
