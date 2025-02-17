package com.backstage.curtaincall.global.exception.category;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {

    private String error;
    private String errorMessage;
}