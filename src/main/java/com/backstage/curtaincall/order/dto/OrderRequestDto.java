package com.backstage.curtaincall.order.dto;

import com.backstage.curtaincall.order.entity.Order;
import com.backstage.curtaincall.order.entity.Status;
import com.backstage.curtaincall.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
    private Long userId;
    private Long productDetailId;
    private int price;
    private List<String> selectedSeats;

    public Order toOrder(User user) {
        return Order.builder()
                .user(user)
                .price(this.price)
                .status(Status.PENDING) // 결제 대기 상태
                .orderNo(String.valueOf(System.currentTimeMillis())) // 랜덤 주문 번호 생성
                .build();
    }
}
