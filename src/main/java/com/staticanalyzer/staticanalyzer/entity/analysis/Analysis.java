package com.staticanalyzer.staticanalyzer.entity.analysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.staticanalyzer.staticanalyzer.entity.project.ProjectVO;

/**
 * 算法分析结果，用于ProjectVO
 * 
 * @author iu_oi
 * @since 0.2
 * @see ProjectVO
 */
@lombok.Getter
@lombok.Setter
@ApiModel(description = "算法分析结果")
public class Analysis {

    @ApiModelProperty(value = "算法类型", example = "UninitializedVariable", required = true)
    private String analyseType;

    @ApiModelProperty(value = "算法得到的最高权值评估", example = "Warning", required = true)
    private AnalysisStatus status;

}
