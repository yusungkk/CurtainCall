package com.backstage.curtaincall.product.entity;

import com.backstage.curtaincall.global.entity.BaseEntity;
import com.backstage.curtaincall.product.dto.ProductRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "상품명을 입력해주세요.")
    @Column(nullable = false, length = 50)
    private String productName;

    // @JoinColumn(name = "category_id", nullable = false)
    // private Long categoryId;

    @NotBlank(message = "공연 장소를 입력해주세요.")
    @Column(nullable = false, length = 50)
    private String place;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false)
    @Min(value = 1, message = "러닝타임은 최소 1분 이상이어야 합니다.")
    private int runningTime;

    @Column(nullable = false)
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    private String casting;

    private String notice;

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
}
