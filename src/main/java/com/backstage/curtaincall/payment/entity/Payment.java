package com.backstage.curtaincall.payment.entity;

import com.backstage.curtaincall.global.entity.BaseEntity;
import com.backstage.curtaincall.order.entity.Order;
import com.backstage.curtaincall.payment.dto.PaymentRequestDto;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Entity
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(nullable = false, unique = true, length = 50)
    private String paymentNo;

    // @Enumerated(EnumType.STRING)
    private String payStatus;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String cardName;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public static Payment create(PaymentRequestDto requestDto, Order order) {
        Payment payment = new Payment();

        payment.paymentNo = requestDto.getPaymentNo();
        payment.payStatus = requestDto.getPayStatus();
        payment.price = requestDto.getPrice();
        payment.cardName = requestDto.getCardName();
        payment.order = order;

        return payment;
    }
}
