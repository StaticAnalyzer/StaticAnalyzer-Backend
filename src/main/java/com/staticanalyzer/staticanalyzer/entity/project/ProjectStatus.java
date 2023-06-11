package com.staticanalyzer.staticanalyzer.entity.project;

import io.swagger.annotations.ApiModel;

/**
 * 项目状态
 * 
 * @author Yang Yu
 * @since 0.1
 */
@ApiModel(description = "项目状态")
public enum ProjectStatus {

    Complete, // 完成

    Queueing, // 未完成

    Error // 完成，但出现错误

}
