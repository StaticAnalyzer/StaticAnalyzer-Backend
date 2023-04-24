package com.staticanalyzer.staticanalyzer.entity.analysis;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 算法分析结果
 * 用于ProjectVO
 * 
 * @see com.staticanalyzer.staticanalyzer.entity.project.ProjectVO
 */
@Data
@ApiModel(description = "算法分析结果")
public class Analysis {

    /**
     * 算法类型
     * 可能作为键值
     */
    @ApiModelProperty(value = "算法类型", example = "UninitializedVariable", required = true)
    private String analyseType;

    /**
     * 算法得到的最高权值结果
     * 
     * @see com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisStatus
     */
    @ApiModelProperty(value = "算法得到的权值最高结果", example = "Warning", required = true)
    private AnalysisStatus status;
}
