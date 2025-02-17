package com.backstage.curtaincall.global.exception.category;


import com.backstage.curtaincall.category.controller.CategoryController;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackageClasses = CategoryController.class)
public class GlobalExceptionHandler {

    //@Valid 검증 실패 시 MethodArgumentNotValidException이 발생.
    ///카테고리 생성 및 수정시 유효성 검증
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        // ErrorResponseDto 생성
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                "유효성 검증 실패", // error
                bindingResult.getFieldError().getDefaultMessage() // errorMessage
        );

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }




    // JSON 파싱 에러
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleJsonParseException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        ErrorResponseDto errorResponseDto;

        // 타입 에러 (InvalidFormatException) 처리
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) cause;

            String fieldName = invalidFormatException.getPath().stream()
                    .findFirst()
                    .map(ref -> ref.getFieldName())
                    .orElse("unknown");

            String invalidValue = invalidFormatException.getValue().toString();
            String expectedType = invalidFormatException.getTargetType().getSimpleName();

            errorResponseDto = new ErrorResponseDto(

                    "입력한 값 '" + invalidValue + "'은(는) 유효하지 않습니다. '" + fieldName + "' 필드는 " + expectedType
                            + " 형식이어야 합니다."
                    , ex.getMessage()
            );
        } else {
            // JSON 파싱 에러 (타입 에러가 아닌 경우)
            errorResponseDto = new ErrorResponseDto(
                    "JSON 파싱 형식이 올바르지 않습니다.",
                    ex.getMessage()
            );
        }

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponseDto> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException: {}", e.getMessage());
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                "NPE",
                e.getMessage()
        );

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage());
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                "기타 서버 내부 오류",
                e.getMessage()
        );

        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

