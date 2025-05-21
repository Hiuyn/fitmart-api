package dev.fitmart.FItMart.exception;

import org.springframework.http.ResponseEntity;

public class ResponseUtils {
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(new ApiResponse<>(200, data));
    }
}
