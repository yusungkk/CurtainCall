package com.backstage.curtaincall.product.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class ProductImage {

    @Id
    @GeneratedValue
    private Long id;

    private String originalFileName;

    private String storeFileName;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
