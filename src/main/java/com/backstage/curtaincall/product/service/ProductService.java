package com.backstage.curtaincall.product.service;

import com.backstage.curtaincall.product.dto.ProductAddReq;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Long addProduct(ProductAddReq addRequest) {

        Product newProduct = Product.createProduct(addRequest);
        productRepository.save(newProduct);

        return newProduct.getId();
    }
}
