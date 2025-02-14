package com.backstage.curtaincall.order.entity;

public enum Status {
    PENDING,       // 결제 대기
    COMPLETED,     // 결제 완료
    CANCELED,      // 주문 취소
    REFUNDED       // 환불 완료
}
