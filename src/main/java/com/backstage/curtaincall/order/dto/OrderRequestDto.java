package com.backstage.curtaincall.order.dto;

import com.backstage.curtaincall.order.entity.Order;
import com.backstage.curtaincall.order.entity.Status;
import com.backstage.curtaincall.user.entity.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OrderRequestDto {
    private Long userId;

    private Long productDetailId;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
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
