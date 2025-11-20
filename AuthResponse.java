package com.hospital.dto;

public class AuthResponse<T> {

    private Integer code;   // 0 = 成功, 其他 = 失败
    private String message;
    private T data;

    public static <T> AuthResponse<T> success(T data) {
        AuthResponse<T> r = new AuthResponse<>();
        r.code = 0;
        r.message = "ok";
        r.data = data;
        return r;
    }

    public static <T> AuthResponse<T> fail(String msg) {
        AuthResponse<T> r = new AuthResponse<>();
        r.code = -1;
        r.message = msg;
        r.data = null;
        return r;
    }

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
