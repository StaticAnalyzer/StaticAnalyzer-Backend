package com.staticanalyzer.staticanalyzer.entity.analysis;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.staticanalyzer.algservice.AnalyseResultEntry;

/**
 * 分析结果单元
 * 
 * @author iu_oi
 * @since 0.0.2
 */
@Data
@ApiModel(description = "分析结果单元")
public class AnalysisResult {

    /* 对应源代码起始行 */
    @ApiModelProperty(value = "对应源代码起始行", required = true)
    private int startLine;

    /* 对应源代码起始列 */
    @ApiModelProperty(value = "对应源代码起始列", required = true)
    private int startColumn;

    /* 对应源代码结束行 */
    @ApiModelProperty(value = "对应源代码结束行", required = true)
    private int endLine;

    /* 对应源代码结束列 */
    @ApiModelProperty(value = "对应源代码结束列", required = true)
    private int endColumn;

    /* 分析结果评估 */
    @ApiModelProperty(value = "分析结果评估", example = "Pass", required = true)
    private AnalysisStatus severity;

    /* 分析建议 */
    @ApiModelProperty(value = "分析建议", required = true)
    private String message;

    /**
     * 通过protobuf类获取信息
     * 
     * @param analyseResultEntry
     * @see AnalyseResultEntry
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
