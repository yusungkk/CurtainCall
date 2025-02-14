package com.backstage.curtaincall.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 상품이 없습니다."),
    FAQ_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 FAQ가 없습니다."),
    FAQ_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 타입이 없습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
