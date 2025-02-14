package com.backstage.curtaincall.specialProduct.entity;

import com.backstage.curtaincall.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name ="special_products")
public class SpecialProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "special_product_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procuct_id", nullable = false)
    TemporaryProduct product;

    private int discountRate; // 할인율 (0~100, 정수만 허용)

    private LocalDateTime startDate; // 할인 시작 날짜

    private LocalDateTime endDate; // 할인 종료 날짜

    @Column(name = "is_deleted")
    private boolean deleted = false; // 삭제 여부 (기본값 FALSE)

}
