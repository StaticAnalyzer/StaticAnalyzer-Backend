package com.staticanalyzer.staticanalyzer.entity;

import lombok.Data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 统一返回对象
 * 
 * @author iu_oi
 * @since 0.0.1
 */
@Data
@ApiModel(description = "封装REST响应数据")
public class Result<D> {

    /**
     * REST响应码
     * 成功 {@static 0}
     * 失败 {@static -1}
     * 需要登录 {@static -2}
     */
    @ApiModelProperty(value = "REST响应码", example = "0", required = true)
    private int code;

    /* REST消息 */
    @ApiModelProperty(value = "REST消息", required = true)
    private String msg;

    /* REST数据 */
    @ApiModelProperty(value = "REST数据", required = false)
    private D data;

    /**
     * 请求成功
     * {@static 0}
     */
    public static int OK = 0;

    /**
     * 生成不带数据的成功消息
     * 
     * @param <D> data类型
     * @param msg
     * @return {@code code = 0, data = null}
     */
    public static <D> Result<D> ok(String msg) {
        Result<D> result = new Result<>();
        result.setCode(Result.OK);
        result.setMsg(msg);
        return result;
    }

    /**
     * 生成带数据的成功消息
     * 
     * @param <D>  data类型
     * @param msg
     * @param data
     * @return {@code code = 0}
     */
    public static <D> Result<D> ok(String msg, D data) {
        Result<D> result = new Result<>();
        result.setCode(Result.OK);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 请求失败
     * {@static -1}
     */
    public static int ERROR = -1;

    /**
     * 生成失败消息
     * 
     * @param <D> data类型
     * @param msg
     * @return {@code code = -1, data = null}
     */
    public static <D> Result<D> error(String msg) {
        Result<D> result = new Result<>();
        result.setCode(Result.ERROR);
        result.setMsg(msg);
        return result;
    }

    /**
     * 需要重新认证
     * {@static -2}
     */
    public static int HINT = -2;

    /**
     * 生成重新认证消息
     * 
     * @param <D> data类型
     * @param msg
     * @return {@code code = -2, data = null}
     */
    public static <D> Result<D> hint(String msg) {
        Result<D> result = new Result<>();
        result.setCode(Result.HINT);
        result.setMsg(msg);
        return result;
    }
}
