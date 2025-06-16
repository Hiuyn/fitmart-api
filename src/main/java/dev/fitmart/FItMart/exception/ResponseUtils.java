package dev.fitmart.FItMart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

public class ResponseUtils {
    public static <T> ResponseEntity<BaseResponse<T>> success(T data) {
        BaseResponse<T> response = new BaseResponse<>(200, "success", data);
        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<BaseResponse<T>> noContent() {
        return new ResponseEntity<>(new BaseResponse<>(200, "success", null), HttpStatus.NO_CONTENT);
    }
}
