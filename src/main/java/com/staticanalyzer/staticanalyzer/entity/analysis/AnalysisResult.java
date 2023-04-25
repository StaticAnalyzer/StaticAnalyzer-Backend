package com.staticanalyzer.staticanalyzer.entity.analysis;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.staticanalyzer.algservice.AnalyseResultEntry;

/**
 * 分析结果单元
 * 
 * @author iu_oi
 * @verion 0.0.1
 */
@Data
@ApiModel(description = "分析结果单元")
public class AnalysisResult {

    /**
     * 对应源代码起始行
     */
    @ApiModelProperty(value = "对应源代码起始行")
    private int startLine;

    /**
     * 对应源代码起始列
     */
    @ApiModelProperty(value = "对应源代码起始列")
    private int startColumn;

    /**
     * 对应源代码结束行
     */
    @ApiModelProperty(value = "对应源代码结束行")
    private int endLine;

    /**
     * 对应源代码结束列
     */
    @ApiModelProperty(value = "对应源代码结束列")
    private int endColumn;

    /**
     * 分析结果评估
     */
    @ApiModelProperty(value = "分析结果评估")
    private AnalysisStatus severity;

    /**
     * 分析报告
     */
    @ApiModelProperty(value = "分析报告")
    private String message;

    /**
     * 通过protobuf规定的返回类获取信息
     * 
     * @param analyseResultEntry
     * @see com.staticanalyzer.algservice.AnalyseResultEntry
     */
    public AnalysisResult(AnalyseResultEntry analyseResultEntry) {
        startLine = analyseResultEntry.getStartLine();
        startColumn = analyseResultEntry.getStartColumn();
        endLine = analyseResultEntry.getEndLine();
        endColumn = analyseResultEntry.getEndColumn();
        severity = AnalysisStatus.valueOf(analyseResultEntry.getSeverity());
        message = analyseResultEntry.getMessage();
    }
}
