package com.backstage.curtaincall.product.repository;

import com.backstage.curtaincall.product.entity.Product;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAll(Pageable pageable);

    Page<Product> findByProductNameContaining(String productName, Pageable pageable);

    //특가상품
//    @Query("SELECT DISTINCT p FROM Product p " +
//            "LEFT JOIN FETCH p.specialProducts sp " +
//            "WHERE p.productId = :productId AND sp.status = 'ACTIVE'")
//    Optional<Product> findWithSpecialProduct(@Param("productId") Long productId);

    //상품관 연관된거 전부 가져오기
    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.specialProducts sp " +
            "LEFT JOIN FETCH p.category c " +
            "LEFT JOIN FETCH p.productImage pi " +
            "LEFT JOIN FETCH p.productDetails pd " +
            "WHERE p.productId = :productId AND sp.status = 'ACTIVE'")
    Optional<Product> findProductWithAll(@Param("productId") Long productId);

}
