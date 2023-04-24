package com.staticanalyzer.staticanalyzer.entity.analysis;

import java.util.List;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.staticanalyzer.algservice.AnalyseResultEntry;
import com.staticanalyzer.staticanalyzer.entity.project.FileEntry;

/**
 * 单个文件分析结果
 */
@Data
@ApiModel(description = "单个文件分析结果")
public class FileAnalysis implements FileEntry {

    /**
     * 文件名
     * 可能作为键值
     */
    @ApiModelProperty(value = "文件名", required = true)
    private String name;

    /**
     * 文件中权值最高的结果
     * 
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisStatus
     */
    @ApiModelProperty(value = "文件中权值最高的结果", example = "Pass", required = false)
    private AnalysisStatus severity;

    /**
     * 源代码
     */
    @ApiModelProperty(value = "源代码", required = true)
    private String src;

    /**
     * 综合所有算法的分析结果列表
     * 用于可视化标注
     * 
     * @see com.staticanalyzer.algservice.AnalyseResultEntry
     */
    @ApiModelProperty(value = "分析结果列表", required = false)
    private List<AnalyseResultEntry> analyseResults;
}
