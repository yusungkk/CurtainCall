package com.backstage.curtaincall.order.dto;

import com.backstage.curtaincall.order.entity.Order;
import com.backstage.curtaincall.order.entity.Status;
import com.backstage.curtaincall.product.dto.ProductDetailResponseDto;
import com.backstage.curtaincall.user.dto.response.UserResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Builder
public class OrderResponseDto {
    private Long orderId;
    private String orderNo;
    private int price;
    private Status status;
    private UserResponse user;
    private List<OrderDetailResponseDto> orderDetails;

    public static OrderResponseDto fromEntity(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .orderNo(order.getOrderNo())
                .user(new UserResponse(order.getUser())) // User 데이터 포함
                .price(order.getPrice())
                .status(order.getStatus())
                .orderDetails(order.getOrderDetails().stream()
                        .map(OrderDetailResponseDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }


}
