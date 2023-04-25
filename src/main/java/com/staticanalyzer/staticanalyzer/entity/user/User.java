package com.staticanalyzer.staticanalyzer.entity.user;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * 用户信息
 * 
 * @author iu_oi
 * @since 0.0.1
 */
@Data
@ApiModel(description = "用户信息")
public class User {

    /* 用户id 由mysql自动生成 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "用户id", required = false)
    private Integer id;

    /**
     * 用户名
     * 必须符合user.username-format的规范
     * 
     * @see UserProperties
     */
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    /**
     * 用户密码
     * 必须符合user.password-format的规范
     * 
     * @see UserProperties
     */
    @ApiModelProperty(value = "用户密码", required = true)
    private String password;
}
