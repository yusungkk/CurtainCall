package com.backstage.curtaincall.product.entity;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Time {
    HOUR_00_00("00:00"),
    HOUR_01_00("01:00"),
    HOUR_02_00("02:00"),
    HOUR_03_00("03:00"),
    HOUR_04_00("04:00"),
    HOUR_05_00("05:00"),
    HOUR_06_00("06:00"),
    HOUR_07_00("07:00"),
    HOUR_08_00("08:00"),
    HOUR_09_00("09:00"),
    HOUR_10_00("10:00"),
    HOUR_11_00("11:00"),
    HOUR_12_00("12:00"),
    HOUR_13_00("13:00"),
    HOUR_14_00("14:00"),
    HOUR_15_00("15:00"),
    HOUR_16_00("16:00"),
    HOUR_17_00("17:00"),
    HOUR_18_00("18:00"),
    HOUR_19_00("19:00"),
    HOUR_20_00("20:00"),
    HOUR_21_00("21:00"),
    HOUR_22_00("22:00"),
    HOUR_23_00("23:00");

    private final String time;

    Time(String time) {
        this.time = time;
    }

    @JsonValue
    public String getTime() {
        return time;
    }


    // 🔹 모든 TimeSlot 값을 List<String>으로 반환
    public static List<String> getAllTimeSlots() {
        return Arrays.stream(Time.values())
                .map(Time::getTime)
                .collect(Collectors.toList());
    }
}
