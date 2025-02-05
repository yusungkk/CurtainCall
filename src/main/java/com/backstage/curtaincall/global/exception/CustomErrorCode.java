package com.backstage.curtaincall.global.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
public enum CustomErrorCode {

    FAQ_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 FAQ가 없습니다."),
    FAQ_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 타입이 없습니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
