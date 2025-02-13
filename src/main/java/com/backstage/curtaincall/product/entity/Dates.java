package com.backstage.curtaincall.product.entity;

import lombok.Getter;

@Getter
public enum Dates {
    MONDAY("월"),
    TUESDAY("화"),
    WEDNESDAY("수"),
    THURSDAY("목"),
    FRIDAY("금"),
    SATURDAY("토"),
    SUNDAY("일");

    private final String korean;

    Dates(String korean) {
        this.korean = korean;
    }

    public static Dates fromKorean(String korean) {
        for (Dates day : values()) {
            if (day.getKorean().equals(korean)) {
                return day;
            }
        }
        throw new IllegalArgumentException("Invalid day: " + korean);
    }
}
