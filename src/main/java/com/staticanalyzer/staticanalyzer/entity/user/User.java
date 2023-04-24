package com.staticanalyzer.staticanalyzer.entity.user;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;

/**
 * 用户信息
 * 用户在数据库中的表示型式
 * 可以被前端查询
 */
@Data
@ApiModel(description = "用户信息")
public class User {

    /**
     * 用户id
     * 由mysql自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "用户id", required = false)
    private int id;

    /**
     * 用户名
     * 必须符合user.username-format的规范
     * 
     * @see com.staticanalyzer.staticanalyzer.config.user.UserProperties
     */
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    /**
     * 用户密码
     * 必须符合user.password-format的规范
     * 
     * @see com.staticanalyzer.staticanalyzer.config.user.UserProperties
     */
    @ApiModelProperty(value = "用户密码", required = true)
    private String password;
}
