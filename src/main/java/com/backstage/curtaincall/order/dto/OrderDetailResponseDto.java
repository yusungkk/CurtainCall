package com.backstage.curtaincall.order.dto;

import com.backstage.curtaincall.order.entity.OrderDetail;
import com.backstage.curtaincall.product.dto.ProductDetailResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderDetailResponseDto {
    private Long orderDetailId;
    private ProductDetailResponseDto productDetail;
    private String seat;

    public static OrderDetailResponseDto fromEntity(OrderDetail orderDetail) {
        return OrderDetailResponseDto.builder()
                .orderDetailId(orderDetail.getOrderDetailId())
                .productDetail(ProductDetailResponseDto.fromEntity(orderDetail.getProductDetail())) // ProductDetail 변환
                .seat(orderDetail.getSeat())
                .build();
    }
}
