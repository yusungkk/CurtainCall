package com.backstage.curtaincall.product.entity;

import com.backstage.curtaincall.product.dto.ProductAddReq;
import com.backstage.curtaincall.productDetail.entity.ProductDetail;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Product {

    @Id
    @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    private String name;

    private Long categoryId;

    private String place;

    private LocalDate startDate;
    private LocalDate endDate;

    private int runningTime;

    private int price;

    private String casting;

    private String notice;

    private boolean isDeleted;

    @OneToMany(mappedBy = "product")
    private List<ProductDetail> productDetails = new ArrayList<>();

    @OneToOne(mappedBy = "product")
    private ProductImage productImage;

    // == 생성 메서드
    public static Product createProduct(ProductAddReq addRequest) {
        Product product = new Product();

        product.name = addRequest.getProductName();

        return product;
    }
}
