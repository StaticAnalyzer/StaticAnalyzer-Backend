package com.staticanalyzer.staticanalyzer.entities;

import com.baomidou.mybatisplus.annotation.*;

import javax.validation.constraints.Size;
import java.util.List;

public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    @Size(min = 2, max = 30, message = "bad username")
    private String username;

    @Size(min = 8, max = 30, message = "bad password")
    private String password;

    @TableField(exist = false)
    private List<Project> projectList;

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

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    @Override
    public String toString() {
        return String.format(
                "User{id=%d,username=%s,password=%s}", id, username, password);
    }
}
