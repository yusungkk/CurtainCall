package com.backstage.curtaincall.order.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class OrderRequestDto {
    private Long userId;
    private Long productDetailId;
    private int price;
    private List<OrderDetailRequestDto> orderDetails; // 주문 상세 목록
}
