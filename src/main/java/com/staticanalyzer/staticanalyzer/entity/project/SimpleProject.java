package com.staticanalyzer.staticanalyzer.entity.project;

import com.staticanalyzer.staticanalyzer.controller.PlaygroundController;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 单文件项目类
 * 
 * @see PlaygroundController
 * @author YangYu
 * @since 0.3
 */
@lombok.Getter
@lombok.Setter
@ApiModel(description = "单文件项目")
public class SimpleProject {

    @ApiModelProperty(value = "源代码", required = true)
    private String code;

    @ApiModelProperty(value = "配置文件", required = true)
    private String config;

}
