package com.backstage.curtaincall.product.entity;

import com.backstage.curtaincall.category.domain.Category;
import com.backstage.curtaincall.global.entity.BaseEntity;
import com.backstage.curtaincall.product.dto.ProductRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Entity
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false, length = 50)
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 50)
    private String place;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false)
    private int runningTime;

    @Column(nullable = false)
    private int price;

    private String casting;

    @Column(nullable = false, length = 500)
    private String notice;

    private Long salesCount;    // 총 판매량

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true) // 부모 삭제시 자동 삭제 + 관계 끊김
    private List<ProductDetail> productDetails = new ArrayList<>();

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProductImage productImage;

    public void update(ProductRequestDto request) {
        this.productName = request.getProductName();
        this.place = request.getPlace();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.runningTime = request.getRunningTime();
        this.price = request.getPrice();
        this.casting = request.getCasting();
        this.notice = request.getNotice();
    }

    public void updateImage(ProductImage image) {
        this.productImage = image;
    }

    public void setSalesCount(long salesCount) {
        this.salesCount = salesCount;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }
}
