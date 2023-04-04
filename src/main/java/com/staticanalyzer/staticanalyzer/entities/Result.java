package com.staticanalyzer.staticanalyzer.entities;

import java.util.Map;

public class Result {
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
        code = 0;
    }

    public Result(int code) {
        this.code = code;
    }

    public static Result success() {
        return new Result(0);
    }

    public static Result failure() {
        return new Result(-1);
    }

}
