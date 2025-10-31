package com.example.appwork.model;

public class BaseResponse<T>{

    private int code;        // 响应码
    private String message;  // 响应消息
    private T data;         // 响应数据

    // 判断请求是否成功
    public boolean isSuccess() {
        return code == 200; // 如果响应码为 200 返回 true
    }

    // code 的 getter
    public int getCode() {
        return code;
    }

    // code 的 setter
    public void setCode(int code) {
        this.code = code;
    }

    // message 的 getter
    public String getMessage() {
        return message;
    }

    // message 的 setter
    public void setMessage(String message) {
        this.message = message;
    }

    // data 的 getter
    public T getData() {
        return data;
    }

    // data 的 setter
    public void setData(T data) {
        this.data = data;
    }
}
