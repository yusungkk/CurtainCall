package com.backstage.curtaincall.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomExResponse> handleCustomEx(CustomException e) {
        return CustomExResponse.toResponse(e.getCustomErrorCode());
    }
}
