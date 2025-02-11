package com.backstage.curtaincall.product.dto;

import com.backstage.curtaincall.product.entity.ProductImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductImageResponseDto {
    private Long imageId;
    private String imageUrl;
    private Long productId;

    public static ProductImageResponseDto fromEntity(ProductImage productImage) {
        return ProductImageResponseDto.builder()
                .imageId(productImage.getImageId())
                .imageUrl(productImage.getImageUrl())
                .productId(productImage.getProduct().getProductId())
                .build();
    }
}
