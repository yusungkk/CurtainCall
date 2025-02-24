package com.backstage.curtaincall.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 상품이 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 주문이 없습니다."),
    EMPTY_IMAGE(HttpStatus.BAD_REQUEST, "이미지는 필수입니다."),
    FAIL_IMAGE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다."),

    FAQ_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 FAQ가 없습니다."),
    FAQ_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 타입이 없습니다."),

    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "일치하는 문의내역이 없습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    IS_NOT_ADMIN(HttpStatus.FORBIDDEN, "관리자 권한이 없습니다."),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

    SEAT_ALREADY_RESERVED(HttpStatus.BAD_REQUEST, "해당 좌석은 이미 선택되었습니다. 다른 좌석으로 다시 시도해주세요."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // 카테고리 관련 에러 코드
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),
    INVALID_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "카테고리 이름은 공백일 수 없습니다."),
    DUPLICATED_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "이미 존재하는 카테고리 이름입니다."),
    INVALID_CATEGORY_OPERATION(HttpStatus.BAD_REQUEST, "카테고리 추가는 루트 카테고리만 할 수 있습니다."),

    //특가상품 관련 에러코드
    SPECIAL_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 특가상품을 찾을 수 없습니다."),
    INVALID_DISCOUNT_PERIOD(HttpStatus.BAD_REQUEST, "할인 기간이 상품의 공연 기간을 벗어났습니다."),

    // 추가적인 글로벌 에러 코드
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "유효성 검증 실패"),
    NPE(HttpStatus.BAD_REQUEST, "Null Pointer Exception"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류"),
    ;

    private final HttpStatus status;
    private final String message;
}
