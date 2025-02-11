package com.backstage.curtaincall.product.dto;

import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.product.entity.ProductImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductImageRequestDto {

    @NotBlank(message = "이미지 URL을 입력해주세요.")
    private String imageUrl;

    @NotNull(message = "상품 ID가 필요합니다.")
    private Long productId;

    public ProductImage toEntity(Product product) {
        return ProductImage.builder()
                .imageUrl(imageUrl)
                .product(product)
                .build();
    }
}
