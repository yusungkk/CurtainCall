package com.backstage.curtaincall.specialProduct.service;

import static com.backstage.curtaincall.global.exception.CustomErrorCode.*;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.specialProduct.dto.SpecialProductDto;
import com.backstage.curtaincall.specialProduct.entity.SpecialProduct;
import com.backstage.curtaincall.specialProduct.repository.SpecialProductRepository;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.product.repository.ProductRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpecialProductService {

    private final SpecialProductRepository specialProductRepository;
    private final ProductRepository productRepository; // Product 조회용

    // 전체 조회
    public List<SpecialProductDto> findAll(){
        List<SpecialProduct> specialProducts = specialProductRepository.findAll();
        return specialProducts.stream()
                .map(SpecialProduct::toDto)
                .toList();
    }

    // 삭제된것만 전체 조회
    public List<SpecialProductDto> findAllDeleted() {
        List<SpecialProduct> specialProducts = specialProductRepository.findAllDeleted();
        return specialProducts.stream()
                .map(SpecialProduct::toDto)
                .toList();
    }

    //단건조회
    @Cacheable(cacheNames = "findByIdWithProduct", key = "'specialProduct:' + #id", cacheManager = "cacheManager")
    public SpecialProductDto findByIdWithProduct(Long id) {
        SpecialProduct sp = specialProductRepository.findByIdWithProduct(id)
                .orElseThrow(() -> new CustomException(SPECIAL_PRODUCT_NOT_FOUND));
        return sp.toDto();
    }

    // 생성
    @Transactional
    public SpecialProductDto save(SpecialProductDto dto) {
        // 연관된 상품(Product) 조회
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.PRODUCT_NOT_FOUND));

        //할인 날짜가 공연날짜 범위를 벗어나면 오류발생
        validateDate(product, dto);

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

    // 매일 자정에 할인 종료 날짜가 오늘 이전인 상품 삭제(아직 삭제되지 않은 경우)
    @Transactional
    public void deleteExpiredSpecialProducts() {
        LocalDate today = LocalDate.now();
        List<SpecialProduct> expiredProducts = specialProductRepository.findAllExpiredSpecialProducts(today);
        expiredProducts.forEach(SpecialProduct::delete);
        System.out.println("Deleted expired products: " + expiredProducts.size());
    }

    //매일 자정에 할인 시작 날짜가 오늘인 상품 redis에 생성
    public void cacheStartingSpecialProducts(RedisTemplate<String, Object> redisTemplate) {
        LocalDate today = LocalDate.now();
        List<SpecialProduct> startingProducts = specialProductRepository.findAllStartingSpecialProducts(today);
        List<SpecialProductDto> dtos = startingProducts.stream()
                .map(SpecialProduct::toDto)
                .collect(Collectors.toList());
        redisTemplate.opsForValue().set("starting_special_products", dtos);
    }

    private void validateDate(Product product, SpecialProductDto dto) {
        if (product.getStartDate().isAfter(dto.getDiscountStartDate()) ||
                product.getEndDate().isBefore(dto.getDiscountEndDate())) {

            throw new CustomException(CustomErrorCode.INVALID_DISCOUNT_PERIOD);
        }
    }




}
