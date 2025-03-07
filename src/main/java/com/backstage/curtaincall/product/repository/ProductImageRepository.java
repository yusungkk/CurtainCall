package com.backstage.curtaincall.product.repository;

import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
