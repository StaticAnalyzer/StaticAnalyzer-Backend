package com.staticanalyzer.staticanalyzer.entity.file;

import com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisResult;
import com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisStatus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 文件分析简报
 * <p>
 * 简报的{@code src}属性永远为{@code null}
 * </p>
 * 
 * @see AnalysisStatus
 * @author YangYu
 * @since 0.2
 */
@lombok.Setter
@lombok.Getter
@ApiModel(description = "文件分析简报")
public class SrcFileDigest extends SrcFile {

    @ApiModelProperty(value = "文件中权值最高的评估", example = "Pass", required = true)
    private AnalysisStatus severity;

    public SrcFileDigest(SrcFile srcFile) {
        name = srcFile.name;
        severity = AnalysisStatus.Pass;
    }

    public SrcFileDigest(SrcFileAnalysis fileAnalysis) {
        name = fileAnalysis.getName();
        severity = AnalysisStatus.Pass;

        for (AnalysisResult analyseResult : fileAnalysis.getAnalyseResults()) {
            AnalysisStatus currentSeverity = analyseResult.getSeverity();
            if (currentSeverity.compareTo(severity) > 0)
                severity = currentSeverity;
        }
    }

}
