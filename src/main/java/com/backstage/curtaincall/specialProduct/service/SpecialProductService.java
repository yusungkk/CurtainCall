package com.backstage.curtaincall.specialProduct.service;

import static com.backstage.curtaincall.global.exception.CustomErrorCode.ALREADY_ACTIVE_SPECIAL_PRODUCT_EXISTS;
import static com.backstage.curtaincall.global.exception.CustomErrorCode.DISCOUNT_END_DATE_BEFORE_START;
import static com.backstage.curtaincall.global.exception.CustomErrorCode.DISCOUNT_PERIOD_EXPIRED;
import static com.backstage.curtaincall.global.exception.CustomErrorCode.DISCOUNT_PERIOD_OUT_OF_PRODUCT_RANGE;
import static com.backstage.curtaincall.global.exception.CustomErrorCode.OVERLAPPING_SPECIAL_PRODUCT_DISCOUNT;
import static com.backstage.curtaincall.global.exception.CustomErrorCode.PRODUCT_NOT_FOUND;
import static com.backstage.curtaincall.global.exception.CustomErrorCode.SPECIAL_PRODUCT_NOT_FOUND;
import static com.backstage.curtaincall.global.exception.CustomErrorCode.UPCOMING_SPECIAL_PRODUCT_NOT_FOUND;

import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.product.repository.ProductRepository;
import com.backstage.curtaincall.specialProduct.dto.SpecialProductDto;
import com.backstage.curtaincall.specialProduct.entity.SpecialProduct;
import com.backstage.curtaincall.specialProduct.entity.SpecialProductStatus;
import com.backstage.curtaincall.specialProduct.repository.SpecialProductRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpecialProductService {

    private final SpecialProductRepository specialProductRepository;
    private final ProductRepository productRepository; // Product 조회용
    private final RedisTemplate<String, SpecialProductDto> redisTemplate;

    //단건조회(삭제되지 않은 것만)
    public SpecialProduct findById(Long id){
        return specialProductRepository.findById(id)
                .orElseThrow(() -> new CustomException(SPECIAL_PRODUCT_NOT_FOUND));
    }

    // 전체 조회
    public List<SpecialProductDto> findAll(){
        List<SpecialProduct> specialProducts = specialProductRepository.findAll();
        return specialProducts.stream()
                .map(SpecialProduct::toDto)
                .toList();
    }

    // Redis에서 캐시된 ACTIVE 특가상품 가져오기
    public List<SpecialProductDto> getActiveSpecialProducts() {
        ValueOperations<String, SpecialProductDto> valueOps = redisTemplate.opsForValue();

        // Redis에서 모든 활성화된 특가 상품 키 가져오기
        Set<String> keys = redisTemplate.keys("specialProductCache::specialProduct:*");

        if (!keys.isEmpty()) {
            List<SpecialProductDto> cachedProducts = keys.stream()
                    .map(valueOps::get)
                    .filter(Objects::nonNull)
                    .toList();

            if (!cachedProducts.isEmpty()) {
                return cachedProducts; // 캐시에 데이터가 있으면 반환
            }
        }

        // 캐시가 비어 있으면 DB에서 조회
        List<SpecialProduct> activeProducts = specialProductRepository.findAllActive();
        List<SpecialProductDto> activeProductsDto = activeProducts.stream()
                .map(SpecialProduct::toDto)
                .toList();

        // Redis에 저장 (TTL 24시간 설정)
        for (SpecialProductDto dto : activeProductsDto) {
            valueOps.set("specialProductCache::specialProduct:" + dto.getSpecialProductId(), dto, Duration.ofHours(24));
        }


        return activeProductsDto;
    }


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

    //상품id와 관련된 모든 특가상품 가져오기
    public List<SpecialProduct> findAllByProductId(Long productId){
        return specialProductRepository.findAllByProductId(productId);
    }


    // 생성
    @Transactional
    public SpecialProductDto save(SpecialProductDto dto) {
        // 통합 검증 메서드
        validate(dto);

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        SpecialProduct sp = SpecialProduct.of(product, dto);
        specialProductRepository.save(sp);
        return sp.toDto();
    }

    // 수정: 캐시 반영 O
    @Transactional
    @CachePut(cacheNames = "specialProductCache", key = "'specialProduct:' + #dto.specialProductId", cacheManager = "cacheManager")
    public SpecialProductDto updateWithCache(SpecialProduct sp, SpecialProductDto dto) {
        sp.update(dto);
        return sp.toDto();
    }

    // 수정: 캐시 업데이트 반영 X
    @Transactional
    public void updateWithOutCache(SpecialProduct sp, SpecialProductDto dto) {
        sp.update(dto);
    }


    // Soft 삭제 : 캐시 반영 O
    @Transactional
    @CacheEvict(cacheNames = "specialProductCache", key = "'specialProduct:' + #id", cacheManager = "cacheManager")
    public SpecialProductDto deleteWithCache(SpecialProduct sp) {
        sp.delete();
        return sp.toDto();
    }

    // Soft 삭제 : 캐시 반영 X
    @Transactional
    public void deleteWithOutCache(SpecialProduct sp) {
//        SpecialProduct sp = specialProductRepository.findById(id)
//                .orElseThrow(() -> new CustomException(SPECIAL_PRODUCT_NOT_FOUND));
        sp.delete();
    }

    // 승인: 캐시에 복구된 엔티티 업데이트
    @Transactional
    @CachePut(cacheNames = "specialProductCache", key = "'specialProduct:' + #id", cacheManager = "cacheManager")
    public SpecialProductDto approve(Long id) {
        SpecialProduct sp = specialProductRepository.findByIdUpcoming(id)
                .orElseThrow(() -> new CustomException(UPCOMING_SPECIAL_PRODUCT_NOT_FOUND));

        // 이미 같은 Product에 ACTIVE 상태의 특가 상품이 있는지 확인
        validateAlreadyActiveProduct(sp.getProduct().getProductId());
        
        // 할인 종료일이 이미 지난 경우 예외 발생
        validateDiscountExpired(sp.getEndDate());
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

    private void validateAlreadyActiveProduct(Long productId) {
        boolean isAlreadyActive = specialProductRepository.existsByProductIdAndStatus(
                productId);

        if (isAlreadyActive) {
            throw new CustomException(ALREADY_ACTIVE_SPECIAL_PRODUCT_EXISTS);
        }
    }

    public void validate(SpecialProductDto dto) {
        // 할인 날짜가 공연날짜 범위를 벗어나면 오류발생
        validateOverDate(dto);
        //한 상품에 2개의 할인적용 날짜가 겹치면 오류발생
        validateOverLappingDate(dto);
        // 할인 종료일이 할인 시작일보다 이전이면 오류발생
        validateEndDateBeforeStart(dto);
    }

    private void validateDiscountExpired(LocalDate discountEndDate) {
        if (LocalDate.now().isAfter(discountEndDate)) {
            throw new CustomException(DISCOUNT_PERIOD_EXPIRED);
        }
    }

    public void validateOverDate(SpecialProductDto dto) {
        if (dto.getStartDate().isAfter(dto.getDiscountStartDate()) ||
                dto.getEndDate().isBefore(dto.getDiscountEndDate())) {
            throw new CustomException(DISCOUNT_PERIOD_OUT_OF_PRODUCT_RANGE);
        }
    }

    private void validateOverLappingDate(SpecialProductDto dto) {
        List<SpecialProduct> overlappingProducts = specialProductRepository.findAllOverlappingDates(
                dto.getProductId(), dto.getSpecialProductId(), dto.getDiscountStartDate(), dto.getDiscountEndDate());
        if (!overlappingProducts.isEmpty()) {
            throw new CustomException(OVERLAPPING_SPECIAL_PRODUCT_DISCOUNT);
        }
    }

    private void validateEndDateBeforeStart(SpecialProductDto dto) {
        if (dto.getDiscountEndDate().isBefore(dto.getDiscountStartDate())) {
            throw new CustomException(DISCOUNT_END_DATE_BEFORE_START);
        }
    }
}
