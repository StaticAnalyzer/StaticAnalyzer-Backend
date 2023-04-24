package com.staticanalyzer.staticanalyzer.entity.analysis;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import com.staticanalyzer.algservice.AnalyseResultEntry;
import com.staticanalyzer.staticanalyzer.entity.project.FileEntry;

/**
 * 单个文件分析报告
 */
@Data
@ApiModel(description = "单个文件分析报告")
public class FileAnalysisDetail implements FileEntry {

    /**
     * 文件名
     * 可能作为键值
     */
    @ApiModelProperty(value = "文件名", required = true)
    private String name;

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

    /**
     * 从文件分析结果生成报告
     * 
     * @param fileAnalysis
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.FileAnalysis
     */
    public FileAnalysisDetail(FileAnalysis fileAnalysis) {
        name = fileAnalysis.getName();
        src = fileAnalysis.getSrc();
        analyseResults = fileAnalysis.getAnalyseResults();
    }
}
