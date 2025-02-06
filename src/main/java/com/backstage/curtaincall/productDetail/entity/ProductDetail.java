package com.backstage.curtaincall.productDetail.entity;

import com.backstage.curtaincall.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
public class ProductDetail {

    @Id
    @GeneratedValue
    @Column(name = "product_detail_id")
    private Long id;

    private LocalDate date;

    private LocalTime time;

    private int remain;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
