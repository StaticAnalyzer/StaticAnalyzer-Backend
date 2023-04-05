package com.staticanalyzer.staticanalyzer.entities;

import java.util.HashMap;
import java.util.Map;

public class ResultBuilder {
    private int code = Result.FAILURE;
    private Map<String, Object> data = new HashMap<>();

    public ResultBuilder setCode(int code) {
        this.code = code;
        return this;
    }

    public ResultBuilder addField(String name, Object value) {
        data.put(name, value);
        return this;
    }

    public Result build() {
        return new Result(code, data);
    }
}
