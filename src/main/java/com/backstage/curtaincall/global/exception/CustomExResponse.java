package com.backstage.curtaincall.global.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
public class CustomExResponse {

    private final int statusCode;
    private final String code;
    private final String message;

    public static ResponseEntity<CustomExResponse> toResponse(CustomErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new CustomExResponse(errorCode.getStatus().value(), errorCode.name(), errorCode.getMessage()));

    }
}
