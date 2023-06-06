package com.staticanalyzer.staticanalyzer.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 统一返回对象
 * <p>{@code 0} 成功</p>
 * <p>{@code -1} 失败</p>
 * <p>{@code -2} 需要登录</p>
 * 
 * @author YangYu
 * @since 0.1
 */
@lombok.Getter
@lombok.Setter
@ApiModel(description = "统一返回对象")
public class Result<D> {

    @ApiModelProperty(value = "REST响应码", example = "0", required = true)
    private int code;

    @ApiModelProperty(value = "REST消息", required = true)
    private String msg;

    @ApiModelProperty(value = "REST数据", required = false)
    private D data;

    public static int OK = 0; // 成功

    public static <D> Result<D> ok(String msg) {
        Result<D> result = new Result<>();
        result.setCode(Result.OK);
        result.setMsg(msg);
        return result;
    }

    public static <D> Result<D> ok(String msg, D data) {
        Result<D> result = new Result<>();
        result.setCode(Result.OK);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static int ERROR = -1; // 失败

    public static <D> Result<D> error(String msg) {
        Result<D> result = new Result<>();
        result.setCode(Result.ERROR);
        result.setMsg(msg);
        return result;
    }

    public static int HINT = -2; // 需要登录

    public static <D> Result<D> hint(String msg) {
        Result<D> result = new Result<>();
        result.setCode(Result.HINT);
        result.setMsg(msg);
        return result;
    }

}
