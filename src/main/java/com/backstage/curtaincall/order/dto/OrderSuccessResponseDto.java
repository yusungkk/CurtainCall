package com.backstage.curtaincall.order.dto;

import com.backstage.curtaincall.product.entity.Time;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderSuccessResponseDto {

    private String orderNo;

    private int orderPrice;

    private String productName;

    private String place;

    private LocalDate performanceDate;

    private Time performanceTime;

    private List<String> seats;

    private String imageUrl;

    public static OrderSuccessResponseDto create(String orderNo, int orderPrice, List<String> seats, String productName, String place, LocalDate performanceDate, Time performanceTime, String imageUrl) {
        OrderSuccessResponseDto dto = new OrderSuccessResponseDto();

        dto.orderNo = orderNo;
        dto.orderPrice = orderPrice;
        dto.productName = productName;
        dto.place = place;
        dto.performanceDate = performanceDate;
        dto.performanceTime = performanceTime;
        dto.seats = seats;
        dto.imageUrl = imageUrl;

        return dto;
    }

}
