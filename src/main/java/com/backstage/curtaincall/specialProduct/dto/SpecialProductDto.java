package com.backstage.curtaincall.specialProduct.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialProductDto {

    // 상품 정보
    private Long productId;
    private String productName;
    private int price;
    private LocalDate productStartDate; // Product의 시작날짜
    private LocalDate productEndDate;   // Product의 종료날짜

    // 할인 정보 (특가상품의 할인 기간)
    private Long specialProductId;
    private int discountRate;
    private LocalDate discountStartDate; // 할인 시작일시
    private LocalDate discountEndDate;   // 할인 종료일시

}
