package com.staticanalyzer.staticanalyzer.entity.analysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.staticanalyzer.algservice.AnalyseResultEntry;

/**
 * 分析结果单元
 * 
 * @author YangYu
 * @since 0.2
 */
@lombok.Setter
@lombok.Getter
@lombok.NoArgsConstructor
@ApiModel(description = "分析结果单元")
public class AnalysisResult {

    @ApiModelProperty(value = "对应源代码起始行", required = true)
    private int startLine;

    @ApiModelProperty(value = "对应源代码起始列", required = true)
    private int startColumn;

    @ApiModelProperty(value = "对应源代码结束行", required = true)
    private int endLine;

    @ApiModelProperty(value = "对应源代码结束列", required = true)
    private int endColumn;

    @ApiModelProperty(value = "分析结果评估", example = "Pass", required = true)
    private AnalysisStatus severity;

    @ApiModelProperty(value = "分析建议", required = true)
    private String message;

    public AnalysisResult(AnalyseResultEntry analyseResultEntry) {
        startLine = analyseResultEntry.getStartLine();
        startColumn = analyseResultEntry.getStartColumn();
        endLine = analyseResultEntry.getEndLine();
        endColumn = analyseResultEntry.getEndColumn();
        severity = AnalysisStatus.valueOf(analyseResultEntry.getSeverity());
        message = analyseResultEntry.getMessage();
    }

}
