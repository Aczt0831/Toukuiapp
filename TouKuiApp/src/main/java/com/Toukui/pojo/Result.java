package com.Toukui.pojo;

import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String msg;
    private Object data;
    
    // 无参构造函数
    public Result() {
    }
    
    // 三参构造函数
    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    
    public static Result success() {
        return new Result(0, "true", null);
    }
    
    public static Result success(Object data) {
        return new Result(0, "true", data);
    }
    
    public static Result error(String msg) {
        return new Result(40000, msg, null);
    }
}
