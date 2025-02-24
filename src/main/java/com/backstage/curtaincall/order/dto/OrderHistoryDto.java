package com.backstage.curtaincall.order.dto;

import com.backstage.curtaincall.product.entity.Time;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderHistoryDto {
    private String orderNo;

    private int orderPrice;

    private String productName;

    private LocalDate performanceDate;

    private Time performanceTime;

    private List<String> seats;

    private String imageUrl;

    public static OrderHistoryDto create(String orderNo, int orderPrice, List<String> seats, LocalDate performanceDate, Time performanceTime, String productName, String imageUrl) {
        OrderHistoryDto orderHistoryDto = new OrderHistoryDto();

        orderHistoryDto.orderNo = orderNo;
        orderHistoryDto.orderPrice = orderPrice;
        orderHistoryDto.seats = seats;
        orderHistoryDto.performanceDate = performanceDate;
        orderHistoryDto.performanceTime = performanceTime;
        orderHistoryDto.productName = productName;
        orderHistoryDto.imageUrl = imageUrl;

        return orderHistoryDto;
    }
}
