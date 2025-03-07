package com.backstage.curtaincall.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {

    private String paymentNo;

    private String payStatus;

    private int price;

    private String cardName;

    private Long orderId;
}
