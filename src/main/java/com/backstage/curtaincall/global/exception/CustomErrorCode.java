package com.backstage.curtaincall.global.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
public enum CustomErrorCode {

    FAQ_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 FAQ가 없습니다."),
    FAQ_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 타입이 없습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),

    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
