package com.backstage.curtaincall.product.dto;

import com.backstage.curtaincall.specialProduct.entity.SpecialProduct;
import com.backstage.curtaincall.specialProduct.entity.SpecialProductStatus;
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
    private Long salesCount;
    private String productImageUrl;
    private List<ProductDetailResponseDto> productDetails;

    // SpecialProduct 정보
    private int discountRate;
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;

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
                .salesCount(product.getSalesCount())
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

    public static ProductResponseDto of(Product product) {

        Optional<SpecialProduct> activeSpecialProduct = product.getSpecialProducts().stream()
                                                               .filter(sp -> sp.getStatus() == SpecialProductStatus.ACTIVE)
                                                               .findFirst();


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
                .salesCount(product.getSalesCount())
                .productImageUrl(product.getProductImage() != null ? product.getProductImage().getImageUrl() : null)
                .productDetails(
                        Optional.ofNullable(product.getProductDetails())  // null 체크
                                .orElse(Collections.emptyList())          // null이면 빈 리스트 반환
                                .stream()
                                .map(ProductDetailResponseDto::fromEntity)
                                .collect(Collectors.toList())
                )
                .discountRate(activeSpecialProduct.map(SpecialProduct::getDiscountRate).orElse(0))
                .discountStartDate(activeSpecialProduct.map(SpecialProduct::getStartDate).orElse(null))
                .discountEndDate(activeSpecialProduct.map(SpecialProduct::getEndDate).orElse(null))

                .build();
    }
}
