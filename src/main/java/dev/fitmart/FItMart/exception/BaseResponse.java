package dev.fitmart.FItMart.exception;

import lombok.Data;

@Data
public class BaseResponse<T> {
    private int code;
    private String status;
    private T data;
    private String message;

    public BaseResponse(int code, String status, T data, String message) {
        this.code = code;
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, String status, T data) {
        this(code, status, data, null);
    }
}

