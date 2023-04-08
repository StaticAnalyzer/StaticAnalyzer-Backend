package com.staticanalyzer.staticanalyzer.entities;

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("响应数据")
public class Result {
    public static int ACCEPTED = 0;
    public static int REJECTED = -1;
    public static int AUTH_FAILED = -2;

    @ApiModelProperty("错误码，0为成功，-1为失败，-2为需要登录")
    private int code;

    @ApiModelProperty("数据, msg信息，user用户对象，token字符串token，project_id项目编号列表，project项目对象")
    private Map<String, Object> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Result(int code, Map<String, Object> data) {
        this.code = code;
        this.data = data;
    }
}
