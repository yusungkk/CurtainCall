package com.backstage.curtaincall.product.repository;

import com.backstage.curtaincall.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAll(Pageable pageable);

    Page<Product> findByProductNameContaining(String productName, Pageable pageable);

    List<Product> findTop5ByCategoryIdOrderBySalesCountDesc(Long mostClickedCategory);
}
