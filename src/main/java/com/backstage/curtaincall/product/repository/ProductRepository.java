package com.backstage.curtaincall.product.repository;

import com.backstage.curtaincall.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
