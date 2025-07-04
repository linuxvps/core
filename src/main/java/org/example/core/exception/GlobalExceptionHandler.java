package org.example.core.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * این متد به صورت خودکار خطای BadCredentialsException را از هر جای برنامه گرفته
     * و یک پاسخ استاندارد JSON با کد 401 برمی‌گرداند.
     */

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        // این همان پیامی است که فرانت‌اند دریافت خواهد کرد
        errorResponse.put("error", "Incorrect username or password");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}