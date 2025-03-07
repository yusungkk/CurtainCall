package com.backstage.curtaincall.order.dto;

import com.backstage.curtaincall.order.entity.Order;
import com.backstage.curtaincall.order.entity.OrderDetail;
import com.backstage.curtaincall.product.entity.ProductDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailRequestDto {
    private Long productDetailId;
    private String seat;

    public OrderDetailRequestDto(String seat) {
        this.seat = seat;
    }

    public OrderDetail toOrderDetail(Order order, ProductDetail productDetail) {
        return OrderDetail.builder()
                .order(order)
                .productDetail(productDetail)
                .seat(this.seat)
                .build();
    }
}
