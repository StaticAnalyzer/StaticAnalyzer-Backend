package com.staticanalyzer.staticanalyzer.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(description = "封装REST响应数据")
public class RestResult<D> {
    public static int OK = 0;
    public static int ERROR = 1;
    public static int NO_AUTH = 2;

    @ApiModelProperty(value = "REST响应状态码", notes = "0:请求通过\n1:请求失败\n2:需要登录", required = true)
    private int code;

    @ApiModelProperty(value = "REST响应信息", required = true)
    private String msg;

    @ApiModelProperty(value = "REST响应数据", required = false)
    private D data;

    public RestResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }
}
