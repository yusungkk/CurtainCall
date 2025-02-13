package com.backstage.curtaincall.product.dto;

import com.backstage.curtaincall.product.entity.Dates;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.product.entity.ProductDetail;
import com.backstage.curtaincall.product.entity.Time;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailRequestDto {

    @NotNull(message = "요일을 선택해주세요.")
    private Dates date;

    @NotNull(message = "시간을 선택해주세요.")
    private Time time;

    @Min(value = 0, message = "좌석 수는 0 이상이어야 합니다.")
    private int remain;

    public ProductDetail toEntity(Product product) {
        return ProductDetail.builder()
                .product(product)
                .dates(date)
                .time(time)
                .remain(remain)
                .build();
    }

    @JsonCreator
    public ProductDetailRequestDto(
            @JsonProperty("date") String date,
            @JsonProperty("time") String time,
            @JsonProperty("remain") int remain) {
        this.date = Dates.fromKorean(date); // 한글을 Enum으로 변환
        this.time = Time.valueOf(time); // 문자열을 Enum으로 변환
        this.remain = remain;
    }

}
