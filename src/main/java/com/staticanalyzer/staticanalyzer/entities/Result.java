package com.staticanalyzer.staticanalyzer.entities;

import java.util.Map;

public class Result {
    public static int SUCCESS = 0;
    public static int FAILURE = -1;

    private int code;
    private Map<String, Object> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getField(String name) {
        return data.get(name);
    }

    public Result setField(String name, Object obj) {
        data.put(name, obj);
        return this;
    }

    public Result() {
        code = Result.SUCCESS;
    }

    public Result(int code) {
        this.code = code;
    }

    public static Result success() {
        return new Result(Result.SUCCESS);
    }

    public static Result failure() {
        return new Result(Result.FAILURE);
    }

}
