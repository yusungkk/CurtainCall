package com.backstage.curtaincall.inquiry.entity;

import lombok.Getter;

@Getter
public enum FaqType {
    TICKET("티켓"), USER("회원"), ETC("기타");

    private final String value;

    FaqType(String value) {
        this.value = value;
    }
}
