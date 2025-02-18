package com.backstage.curtaincall.product.dto;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.backstage.curtaincall.category.dto.CategoryDto;
import com.backstage.curtaincall.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponseDto {
    private Long productId;
    private String productName;
    private CategoryDto category;
    private String place;
    private LocalDate startDate;
    private LocalDate endDate;
    private int runningTime;
    private int price;
    private String casting;
    private String notice;
    private String productImageUrl;
    private List<ProductDetailResponseDto> productDetails;

    public static ProductResponseDto fromEntity(Product product) {
        return ProductResponseDto.builder()
                .productId(product.getProductId())
                .category(CategoryDto.fromEntity(product.getCategory()))
                .productName(product.getProductName())
                .place(product.getPlace())
                .startDate(product.getStartDate())
                .endDate(product.getEndDate())
                .runningTime(product.getRunningTime())
                .price(product.getPrice())
                .casting(product.getCasting())
                .notice(product.getNotice())
                .productImageUrl(product.getProductImage() != null ? product.getProductImage().getImageUrl() : null)
                .productDetails(
                        Optional.ofNullable(product.getProductDetails())  // null 체크
                                .orElse(Collections.emptyList())          // null이면 빈 리스트 반환
                                .stream()
                                .map(ProductDetailResponseDto::fromEntity)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
