package com.backstage.curtaincall.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private CustomErrorCode customErrorCode;

    public CustomException(CustomErrorCode customErrorCode) {
        super(customErrorCode.getMessage());  // 메시지를 상위 예외에 전달
        this.customErrorCode = customErrorCode;
    }
}
