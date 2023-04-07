package com.staticanalyzer.staticanalyzer.entities;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;

@ApiModel("用户类")
public class User {
    @ApiModelProperty("用户编号")
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @TableField(exist = false)
    @ApiModelProperty("任务编号列表")
    private List<Integer> projectIdList;

    public List<Integer> getProjectIdList() {
        return projectIdList;
    }

    public void setProjectIdList(List<Integer> projectIdList) {
        this.projectIdList = projectIdList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "{id:" + id + ",username:" + username + ",password:" + password + "}";
    }
}
