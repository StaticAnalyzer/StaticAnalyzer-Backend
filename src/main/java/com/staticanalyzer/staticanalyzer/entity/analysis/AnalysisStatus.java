package com.staticanalyzer.staticanalyzer.entity.analysis;

import io.swagger.annotations.ApiModel;

/**
 * 分析评估
 * 
 * @author YangYu
 * @since 0.2
 */
@ApiModel(description = "分析评估")
public enum AnalysisStatus {

    AnalyseError, // 结果中存在一个或多个分析失败的文件

    Pass, // 分析通过

    Hint, // 建议

    Info, // 分析信息

    Warning, // 警告

    Error // 错误

}
