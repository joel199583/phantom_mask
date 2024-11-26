package com.kdan.demo.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("code", HttpStatus.BAD_REQUEST.value(), "msg", "Invalid request, missing or incorrect body content."));
    }
	
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
        		.badRequest()
        		.body(Map.of("code", HttpStatus.BAD_REQUEST.value(), "msg", "Invalid " + ex.getName()));
    }
	
	@ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
        		.badRequest()
                .body(Map.of("code", HttpStatus.BAD_REQUEST.value(), "msg", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        e.printStackTrace();
        return ResponseEntity
        		.status(HttpStatus.INTERNAL_SERVER_ERROR)
        		.body(Map.of("code", HttpStatus.INTERNAL_SERVER_ERROR.value(), "msg", "Please notify the system administrator."));
    }
}
