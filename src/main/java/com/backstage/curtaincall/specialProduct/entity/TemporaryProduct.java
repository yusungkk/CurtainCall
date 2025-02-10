package com.backstage.curtaincall.specialProduct.entity;

import com.backstage.curtaincall.category.domain.Category;
import com.backstage.curtaincall.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TemporaryProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId; // 상품 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // 카테고리 (외래 키)

    @Column(nullable = false, length = 255)
    private String name; // 상품명

    @Column(nullable = false, length = 255)
    private String place; // 장소

    @Column(nullable = false)
    private LocalDateTime startDate; // 시작 날짜

    @Column(nullable = false)
    private LocalDateTime endDate; // 종료 날짜

    @Column(nullable = false)
    private Integer runningTime; // 상영/운영 시간

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // 가격

    @Column(nullable = false, columnDefinition = "TEXT")
    private String casting; // 출연진

    @Column(columnDefinition = "TEXT")
    private String notice; // 공지사항 (NULL 허용)

    @Column(nullable = false)
    private Boolean isDeleted = false; // 삭제 여부 (기본값 FALSE)

    @Column(nullable = false)
    private Boolean isSpecial = false; // 특가 여부 (기본값 FALSE)


}
