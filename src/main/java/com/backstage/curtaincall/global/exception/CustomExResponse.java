package com.backstage.curtaincall.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
@Getter
public class CustomExResponse {

    private final int statusCode;
    private final String code;
    private final String message;

    public static ResponseEntity<CustomExResponse> toResponse(CustomErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new CustomExResponse(errorCode.getStatus().value(), errorCode.name(), errorCode.getMessage()));

    }


    // 에러 코드와 추가 메시지를 전달하는 경우
    public static ResponseEntity<CustomExResponse> toResponse(CustomErrorCode errorCode, String message) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(new CustomExResponse(errorCode.getStatus().value(), errorCode.name(), message));

    }
}
