package com.backstage.curtaincall.specialProduct.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialProductDto {

    // Product 정보
    private Long productId;
    private String productName;
    private int price;

    private LocalDate startDate;
    private LocalDate endDate;

    // SpecialProduct 정보
    private Long specialProductId;

    @Min(value = 0, message = "할인율은 {value}% 이상이어야 합니다.")
    @Max(value = 100, message = "할인율은 {value}% 이하이어야 합니다.")
    private int discountRate;


    private LocalDate discountStartDate; // 할인 시작일시


    private LocalDate discountEndDate;   // 할인 종료일시

    // ProductDetail 정보
//    private LocalDate performanceDate; //해당 공연날짜

}
