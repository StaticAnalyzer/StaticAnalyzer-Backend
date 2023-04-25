package com.staticanalyzer.staticanalyzer.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户认证信息
 * 登录/注册后被发给前端
 * 
 * @author iu_oi
 * @since 0.0.1
 */
@Data
@AllArgsConstructor
@ApiModel(description = "用户认证信息")
public class Identity {

    /**
     * 用户信息
     * 
     * @see User
     */
    @ApiModelProperty(value = "用户信息", required = true)
    private User user;

    /**
     * 用用户id签名的令牌
     * 在一段时间后过期
     * 
     * @see JwtUtils
     */
    @ApiModelProperty(value = "令牌", required = true)
    private String token;
}
