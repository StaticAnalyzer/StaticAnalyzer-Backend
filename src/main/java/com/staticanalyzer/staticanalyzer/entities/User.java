package com.staticanalyzer.staticanalyzer.entities;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    @Length(min = 2, max = 30, message = "用户名长度需要在{min}和{max}之间")
    private String username;

    @Length(min = 8, max = 30, message = "密码长度需要在{min}和{max}之间")
    private String password;

    @TableField(exist = false)
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
        return String.format("User{id=%d,username=%s,password=%s}", id, username, password);
    }
}
