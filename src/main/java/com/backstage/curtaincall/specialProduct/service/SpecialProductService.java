package com.backstage.curtaincall.specialProduct.service;

import static com.backstage.curtaincall.global.exception.CustomErrorCode.*;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.specialProduct.dto.SpecialProductDto;
import com.backstage.curtaincall.specialProduct.entity.SpecialProduct;
import com.backstage.curtaincall.specialProduct.repository.SpecialProductRepository;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.product.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpecialProductService {

    private final SpecialProductRepository specialProductRepository;
    private final ProductRepository productRepository; // Product 조회용

    // 전체 조회
    public List<SpecialProductDto> findAllWithProduct(){
        List<SpecialProduct> specialProducts = specialProductRepository.findAllWithProduct();
        return specialProducts.stream()
                .map(SpecialProduct::toDto)
                .toList();
    }

    // 생성
    @Transactional
    public SpecialProductDto createSpecialProduct(SpecialProductDto dto) {
        // 연관된 상품(Product) 조회
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + dto.getProductId()));

        SpecialProduct sp = SpecialProduct.of(product, dto);
        specialProductRepository.save(sp);
        return sp.toDto();
    }

    // 수정
    @Transactional
    public SpecialProductDto update(SpecialProductDto dto) {
        SpecialProduct sp = specialProductRepository.findById(dto.getSpecialProductId())
                .orElseThrow(() -> new CustomException(SPECIAL_PRODUCT_NOT_FOUND));
        sp.update(dto);
        return sp.toDto();
    }

    //Soft 삭제
    @Transactional
    public void delete(Long id) {
        SpecialProduct sp = specialProductRepository.findById(id)
                .orElseThrow(() -> new CustomException(SPECIAL_PRODUCT_NOT_FOUND));
        sp.delete();
    }

    //복구
    @Transactional
    public void restore(Long id) {
        SpecialProduct sp = specialProductRepository.findByIdDeleted(id)
                .orElseThrow(() -> new CustomException(SPECIAL_PRODUCT_NOT_FOUND));
        sp.restore();
    }
}
