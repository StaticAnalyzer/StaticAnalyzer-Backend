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
