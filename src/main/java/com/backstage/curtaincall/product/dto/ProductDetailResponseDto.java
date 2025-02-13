package com.backstage.curtaincall.product.dto;

import com.backstage.curtaincall.product.entity.Dates;
import com.backstage.curtaincall.product.entity.ProductDetail;
import com.backstage.curtaincall.product.entity.Time;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ProductDetailResponseDto {
    private Long productDetailId;
    private Dates dates;
    private Time time;
    private int remain;
    private Long productId;
    private LocalDate performanceDate;

    public static ProductDetailResponseDto fromEntity(ProductDetail productDetail) {
        return ProductDetailResponseDto.builder()
                .productDetailId(productDetail.getProductDetailId())
                .dates(productDetail.getDates())
                .time(productDetail.getTime())
                .remain(productDetail.getRemain())
                .productId(productDetail.getProduct().getProductId())
                .performanceDate(productDetail.getPerformanceDate())
                .build();
    }
}
