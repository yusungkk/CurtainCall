package com.backstage.curtaincall.order.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderDetailRequestDto {
    private Long productDetailId;
    private int seatNum;
}
