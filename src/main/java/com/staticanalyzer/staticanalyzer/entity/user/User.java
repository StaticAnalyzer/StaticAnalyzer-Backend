package com.staticanalyzer.staticanalyzer.entity.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * 用户类
 * <p>
 * {@code id}由mysql自动生成
 * </p>
 * 
 * @author YangYu
 * @since 0.1
 */
@lombok.Getter
@lombok.Setter
@ApiModel(description = "用户")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "用户id", required = false)
    private Integer id;

    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @ApiModelProperty(value = "用户密码", required = true)
    private String password;

}
