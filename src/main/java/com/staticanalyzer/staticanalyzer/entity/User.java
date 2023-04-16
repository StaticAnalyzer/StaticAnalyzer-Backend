package com.staticanalyzer.staticanalyzer.entity;

import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;

@Data
@ApiModel(description = "封装用户数据")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "用户id", required = false)
    private int id;

    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @ApiModelProperty(value = "用户密码", required = true)
    private String password;
}
