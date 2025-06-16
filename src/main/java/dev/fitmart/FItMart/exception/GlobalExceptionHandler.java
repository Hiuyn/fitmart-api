package dev.fitmart.FItMart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<BaseResponse<List<Object>>> handleApiException(ApiException ex) {
        BaseResponse<List<Object>> errorResponse = new BaseResponse<>(
                ex.getStatus().value(),
                "error",
                Collections.emptyList(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<List<Object>>> handleGenericException(Exception ex) {
        BaseResponse<List<Object>> errorResponse = new BaseResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error",
                Collections.emptyList(),
                "An unexpected error occurred"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        BaseResponse<Map<String, String>> errorResponse = new BaseResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                "error",
                errors,
                "Validation failed"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<List<Object>>> handleBadCredentials(BadCredentialsException ex) {
        BaseResponse<List<Object>> errorResponse = new BaseResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                "error",
                Collections.emptyList(),
                "Invalid username or password"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}

