package com.example.demo.common;

import lombok.Data;

// @Data 自动生成 Getter/Setter
@Data
public class Result<T> {
    private Integer code; // 200成功，400失败
    private String msg;   // 提示消息
    private T data;       // 具体数据

    // 成功的快捷方法
    public static <T> Result<T> success(T data, String msg) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.msg = msg;
        r.data = data;
        return r;
    }

    // 失败的快捷方法
    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.code = 400;
        r.msg = msg;
        return r;
    }
}