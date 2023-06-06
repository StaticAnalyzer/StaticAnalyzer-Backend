package com.staticanalyzer.staticanalyzer.entity.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户可视类
 * 
 * @author YangYu
 * @since 0.1
 */
@lombok.Setter
@lombok.Getter
@lombok.AllArgsConstructor
@ApiModel(description = "用户认证信息")
public class UserWithJwt {

    @ApiModelProperty(value = "用户", required = true)
    private User user;

    @ApiModelProperty(value = "令牌", required = true)
    private String token;

}
