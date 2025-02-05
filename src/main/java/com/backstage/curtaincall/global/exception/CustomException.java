package com.backstage.curtaincall.global.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomException extends RuntimeException {
    private CustomErrorCode customErrorCode;
}
