package com.staticanalyzer.staticanalyzer.entity.analysis;

import java.util.List;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.staticanalyzer.staticanalyzer.entity.project.FileEntry;

/**
 * 文件分析结果
 * 
 * @author iu_oi
 * @since 0.0.2
 * @see com.staticanalyzer.staticanalyzer.entity.project.FileEntry
 */
@Data
@ApiModel(description = "文件分析结果")
public class FileAnalysis implements FileEntry {

    /* 文件名 */
    @ApiModelProperty(value = "文件名", required = true)
    private String name;

    /* 源代码 */
    @ApiModelProperty(value = "源代码", required = true)
    private String src;

    /**
     * 综合所有算法的分析结果列表
     * 用于可视化标注
     * 
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisResult
     */
    @ApiModelProperty(value = "分析结果列表", required = false)
    private List<AnalysisResult> analyseResults;
}
