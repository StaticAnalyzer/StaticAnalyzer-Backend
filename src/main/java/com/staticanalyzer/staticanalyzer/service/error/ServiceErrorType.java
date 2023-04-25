package com.staticanalyzer.staticanalyzer.service.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务异常类型
 * 
 * @author iu_oi
 * @since 0.0.3
 */
@Getter
@AllArgsConstructor
public enum ServiceErrorType {

    /**
     * 找不到用户
     */
    USER_NOT_FOUND("找不到用户"),

    /**
     * 用户已存在
     */
    USER_ALREADY_EXISTS("用户已存在"),

    /**
     * 用户名格式错误
     */
    BAD_USERNAME("用户名格式错误"),

    /**
     * 密码格式错误
     */
    BAD_PASSWORD("密码格式错误"),

    /**
     * 登录超时
     */
    USER_TOKEN_EXPIRED("登录超时"),

    /**
     * 身份验证失败
     */
    USER_AUTH_FAILED("身份验证失败"),

    /**
     * 找不到项目
     */
    PROJECT_NOT_FOUND("找不到项目"),

    /**
     * 项目格式错误
     */
    BAD_PROJECT("项目格式错误"),

    /**
     * 找不到文件
     */
    FILE_NOT_FOUND("找不到文件"),

    /**
     * 未知错误
     */
    UNKNOWN("未知错误");

    /* 报错信息 */
    private String msg;
}
