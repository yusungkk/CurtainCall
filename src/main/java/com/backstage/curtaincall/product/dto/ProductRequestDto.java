package com.backstage.curtaincall.product.dto;

import com.backstage.curtaincall.product.entity.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    @NotBlank(message = "상품명을 입력해주세요.")
    private String productName;

    @NotBlank(message = "카테고리를 선택해주세요.")
    private Long categoryId;

    @NotBlank(message = "공연 장소를 입력해주세요.")
    private String place;

    private LocalDate startDate;
    private LocalDate endDate;

    @Min(value = 1, message = "러닝타임은 최소 1분 이상이어야 합니다.")
    private int runningTime;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    private String casting;
    private String notice;

    private List<ProductDetailRequestDto> productDetails;


    public Product toEntity() {
        return Product.builder()
                .productName(productName)
                .place(place)
                .startDate(startDate)
                .endDate(endDate)
                .runningTime(runningTime)
                .price(price)
                .casting(casting)
                .notice(notice)
                .salesCount(0L)
                .build();
    }
}
