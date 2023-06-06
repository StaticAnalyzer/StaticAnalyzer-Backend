package com.staticanalyzer.staticanalyzer.service.error;

@lombok.Getter
@lombok.AllArgsConstructor
public enum ServiceErrorType {

    USER_NOT_FOUND("找不到用户"),

    USER_ALREADY_EXISTS("用户已存在"),

    BAD_USERNAME("用户名格式错误"),

    BAD_PASSWORD("密码格式错误"),

    USER_TOKEN_EXPIRED("登录超时"),

    USER_AUTH_FAILED("身份验证失败"),

    PROJECT_NOT_FOUND("找不到项目"),

    BAD_PROJECT("项目格式错误"),

    FILE_NOT_FOUND("找不到文件"),

    UNKNOWN("未知错误");

    private String msg;

}
