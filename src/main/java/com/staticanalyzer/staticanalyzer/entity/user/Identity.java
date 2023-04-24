package com.staticanalyzer.staticanalyzer.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户认证信息
 * 登录/注册后被发给前端
 */
@Getter
@Setter
@AllArgsConstructor
@ApiModel(description = "用户认证信息")
public class Identity {

    /**
     * 用户信息
     * 
     * @see com.staticanalyzer.staticanalyzer.entity.user.User
     */
    @ApiModelProperty(value = "用户信息", required = true)
    private User user;

    /**
     * 用用户id签名的令牌
     * 在一段时间后过期
     * 
     * @see com.staticanalyzer.staticanalyzer.utils.JwtUtils
     */
    @ApiModelProperty(value = "令牌", required = true)
    private String token;
}
