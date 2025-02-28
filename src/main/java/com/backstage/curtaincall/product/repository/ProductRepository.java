package com.backstage.curtaincall.product.repository;

import com.backstage.curtaincall.product.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAll(Pageable pageable);

    @Query("select p from Product p where p.productName like %:productName% and p.endDate >= CURRENT_DATE ")
    Page<Product> findByProductNameContaining(String productName, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.specialProducts sp " +
            "LEFT JOIN FETCH p.category c " +
            "LEFT JOIN FETCH p.productImage pi " +
            "WHERE p.productId = :productId")
    Optional<Product> findAllWithoutDetails(@Param("productId") Long productId);


    List<Product> findTop5ByCategoryIdOrderBySalesCountDesc(Long mostClickedCategory);

    @Query("select p from Product p join p.category c1 join c1.parent c2 where c2.name = :genre and p.endDate >= CURRENT_DATE ")
    Page<Product> findByCategoryName(String genre, Pageable pageable);

    // 상품 이름으로 검색
    List<Product> findByProductNameContaining(String keyword);

    @Query("select p from Product p where p.endDate >= CURRENT_DATE")
    Page<Product> findAllNotEnd(Pageable pageable);
}
