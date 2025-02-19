package com.backstage.curtaincall.specialProduct.entity;

import com.backstage.curtaincall.global.entity.BaseEntity;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.specialProduct.dto.SpecialProductDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name ="special_products")
@Builder
public class SpecialProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "special_product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procuct_id", nullable = false)
    private Product product;

    private int discountRate; // 할인율 (0~100, 정수만 허용)

    private LocalDate startDate; // 할인 시작 날짜

    private LocalDate endDate; // 할인 종료 날짜

    @Column(name = "is_deleted")
    private boolean deleted = false;

    public static SpecialProduct of(Product product, SpecialProductDto dto) {
        return SpecialProduct.builder()
                .product(product)
                .discountRate(dto.getDiscountRate())
                .startDate(dto.getDiscountStartDate())
                .endDate(dto.getDiscountEndDate())
                .deleted(false)  // 신규 생성 시 삭제 플래그 false
                .build();
    }


    public SpecialProductDto toDto(){
        return SpecialProductDto.builder()
                .specialProductId(this.id)
                .productId(this.product.getProductId())
                .productName(this.product.getProductName())
                .price(this.product.getPrice())
                .startDate(this.product.getStartDate())
                .endDate(this.product.getEndDate())
                .discountRate(this.discountRate)
                .discountStartDate(this.startDate)
                .discountEndDate(this.endDate)
                .build();
    }

    public void update(SpecialProductDto dto) {
        this.discountRate = dto.getDiscountRate();
        this.startDate = dto.getDiscountStartDate();
        this.endDate  = dto.getDiscountEndDate();
    }

    public void delete() {
        this.deleted = true;
    }

    public void restore() {
        this.deleted = false;
    }
}
