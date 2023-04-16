package com.staticanalyzer.staticanalyzer.model;

import com.staticanalyzer.staticanalyzer.entity.User;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(description = "用户认证信息")
public class RestSession {

    @ApiModelProperty(value = "用户信息", required = true)
    private User user;

    @ApiModelProperty(value = "令牌", name = "jwt", required = true)
    private String token;
}
