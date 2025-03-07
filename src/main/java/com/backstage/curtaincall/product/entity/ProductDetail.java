package com.backstage.curtaincall.product.entity;

import com.backstage.curtaincall.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Entity
public class ProductDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_detail_id")
    private Long productDetailId;

    @Enumerated(EnumType.STRING)
    @Column(name = "dates")
    private Dates dates;

    @Enumerated(EnumType.STRING)
    @Column(name = "time")
    private Time time;

    @Column(name = "remain")
    private int remain;

    private LocalDate performanceDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public void updateRemain(int updatedRemain) {
        remain = updatedRemain;
    }
}
