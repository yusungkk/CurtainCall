package com.backstage.curtaincall.specialProduct.service;

import static com.backstage.curtaincall.global.exception.CustomErrorCode.*;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.specialProduct.dto.SpecialProductDto;
import com.backstage.curtaincall.specialProduct.entity.SpecialProduct;
import com.backstage.curtaincall.specialProduct.entity.SpecialProductStatus;
import com.backstage.curtaincall.specialProduct.repository.SpecialProductRepository;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.product.repository.ProductRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpecialProductService {

    private final SpecialProductRepository specialProductRepository;
    private final ProductRepository productRepository; // Product 조회용
//    private final RedisTemplate<String, SpecialProduct> redisTemplate;

    // 전체 조회
    public List<SpecialProductDto> findAll(){
        List<SpecialProduct> specialProducts = specialProductRepository.findAll();
        return specialProducts.stream()
                .map(SpecialProduct::toDto)
                .toList();
    }

    // Redis에서 캐시된 ACTIVE 특가상품 가져오기
//    public List<SpecialProductDto> getActiveSpecialProducts() {
//        ValueOperations<String, SpecialProduct> valueOps = redisTemplate.opsForValue();
//
//        // Redis에서 모든 활성화된 특가 상품 키 가져오기
//        Set<String> keys = redisTemplate.keys("specialProduct:*");
//
//        if (keys != null && !keys.isEmpty()) {
//            List<SpecialProductDto> cachedProducts = keys.stream()
//                    .map(valueOps::get)
//                    .filter(product -> product != null)
//                    .map(SpecialProduct::toDto) // SpecialProduct -> SpecialProductDto 변환
//                    .toList();
//
//            if (!cachedProducts.isEmpty()) {
//                return cachedProducts; // 캐시에 데이터가 있으면 반환
//            }
//        }
//
//        // 캐시가 비어 있으면 DB에서 조회
//        List<SpecialProduct> activeProducts = specialProductRepository.findAllActive();
//        List<SpecialProductDto> activeProductsDto = activeProducts.stream()
//                .map(SpecialProduct::toDto)
//                .toList();
//
//        // Redis에 저장 (TTL 24시간 설정)
//        for (SpecialProduct product : activeProducts) {
//            valueOps.set("specialProduct:" + product.getId(), product, Duration.ofHours(24));
//        }
//
//        return activeProductsDto;
//    }


    // 이름 검색 및 페이지네이션을 적용한 전체 조회
    @Transactional(readOnly = true)
    public Page<SpecialProductDto> getSpecialProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SpecialProduct> spPage = specialProductRepository.findAll(keyword, pageable);
        return spPage.map(SpecialProduct::toDto);
    }

    // 삭제된것만 전체 조회
    public List<SpecialProductDto> findAllDeleted() {
        List<SpecialProduct> specialProducts = specialProductRepository.findAllDeleted();
        return specialProducts.stream()
                .map(SpecialProduct::toDto)
                .toList();
    }

    // 단건조회 (캐시 적용)
    @Cacheable(cacheNames = "specialProductCache", key = "'specialProduct:' + #id", cacheManager = "cacheManager")
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

        // 할인 날짜가 공연날짜 범위를 벗어나면 오류발생
        validateoverDate(product, dto);
        //한 상품에 2개의 할인적용 날짜가 겹치면 오류발생
        validateOverLappingDate(product, dto);

        SpecialProduct sp = SpecialProduct.of(product, dto);
        specialProductRepository.save(sp);
        return sp.toDto();
    }

    // 수정: 캐시 업데이트 반영
    @Transactional
    @CachePut(cacheNames = "specialProductCache", key = "'specialProduct:' + #dto.specialProductId", cacheManager = "cacheManager")
    public SpecialProductDto update(SpecialProductDto dto) {
        SpecialProduct sp = specialProductRepository.findById(dto.getSpecialProductId())
                .orElseThrow(() -> new CustomException(SPECIAL_PRODUCT_NOT_FOUND));
        sp.update(dto);
        return sp.toDto();
    }

    // Soft 삭제 : 캐시에서 해당 항목 제거
    @Transactional
    @CacheEvict(cacheNames = "specialProductCache", key = "'specialProduct:' + #id", cacheManager = "cacheManager")
    public SpecialProductDto delete(Long id) {
        SpecialProduct sp = specialProductRepository.findById(id)
                .orElseThrow(() -> new CustomException(SPECIAL_PRODUCT_NOT_FOUND));
        sp.delete();
        return sp.toDto();
    }

    // 승인: 캐시에 복구된 엔티티 업데이트
    @Transactional
    @CachePut(cacheNames = "specialProductCache", key = "'specialProduct:' + #id", cacheManager = "cacheManager")
    public SpecialProductDto approve(Long id) {
        SpecialProduct sp = specialProductRepository.findByIdUpcoming(id)
                .orElseThrow(() -> new CustomException(SPECIAL_PRODUCT_NOT_FOUND));

        //현재날짜가 할인기간에 포함되는지 확인
        validateCurrentDate(sp.getStartDate(), sp.getEndDate());
        sp.approve();
        return sp.toDto();
    }

    //승인 취소
    @Transactional
    @CacheEvict(cacheNames = "specialProductCache", key = "'specialProduct:' + #id", cacheManager = "cacheManager")
    public SpecialProductDto approveCancel(Long id) {
        SpecialProduct sp = specialProductRepository.findByIdActive(id)
                .orElseThrow(() -> new CustomException(SPECIAL_PRODUCT_NOT_FOUND));

        sp.approveCancel();// 다시 할인 예정 상태로 변경
        return sp.toDto();
    }

    // 매일 자정에 할인 종료 날짜가 오늘 이전인 상품 삭제(아직 삭제되지 않은 경우)
    @Transactional
    public void deleteExpiredSpecialProducts() {
        LocalDate today = LocalDate.now();
        specialProductRepository.deleteExpiredSpecialProducts(today);
    }

    // 매일 자정에 할인 시작 날짜가 오늘인 상품 redis에 생성
    @Transactional
    public void createStartingSpecialProducts(RedisTemplate<String, Object> redisTemplate) {
        LocalDate today = LocalDate.now();
        List<SpecialProduct> startingProducts = specialProductRepository.findAllStartingSpecialProducts(today);
        List<SpecialProductDto> dtos = startingProducts.stream()
                .map(SpecialProduct::toDto)
                .collect(Collectors.toList());
        redisTemplate.opsForValue().set("specialProduct", dtos);
    }

    public void validateCurrentDate(LocalDate startDate, LocalDate endDate) {
        LocalDate now = LocalDate.now();
        if (now.isBefore(startDate) || now.isAfter(endDate)) {
            throw new CustomException(CustomErrorCode.INVALID_DISCOUNT_DATE);
        }
    }

    private void validateoverDate(Product product, SpecialProductDto dto) {
        if (product.getStartDate().isAfter(dto.getDiscountStartDate()) ||
                product.getEndDate().isBefore(dto.getDiscountEndDate())) {
            throw new CustomException(CustomErrorCode.DISCOUNT_OUT_OF_RANGE);
        }
    }

    private void validateOverLappingDate(Product product, SpecialProductDto dto) {
        List<SpecialProduct> overlappingProducts = specialProductRepository.findAllOverlappingDates(
                product.getProductId(), dto.getDiscountStartDate(), dto.getDiscountEndDate());
        if (!overlappingProducts.isEmpty()) {
            throw new CustomException(CustomErrorCode.OVERLAPPING_DISCOUNT_PERIOD);
        }
    }
}
