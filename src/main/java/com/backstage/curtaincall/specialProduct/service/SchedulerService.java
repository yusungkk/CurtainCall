package com.backstage.curtaincall.specialProduct.service;

import com.backstage.curtaincall.specialProduct.entity.SpecialProduct;
import com.backstage.curtaincall.specialProduct.repository.SpecialProductRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {

    private final SpecialProductRepository specialProductRepository;
    private final SpecialProductService specialProductService;

    @Transactional
    public void deleteExpiredSpecialProducts() {
        LocalDate today = LocalDate.now();

        // 1. 만료된 특가 상품 조회
        List<Long> expiredSpecialProductIds = specialProductRepository.findExpiredSpecialProductIds(today);

        if (!expiredSpecialProductIds.isEmpty()) {
            log.info("🔥 만료된 특가 상품 개수: {}", expiredSpecialProductIds.size());

            // 2. 만료된 상품 Redis 캐시와 DB에서 삭제
            for (Long spId : expiredSpecialProductIds) {
                specialProductService.delete(spId);
            }

        }
    }

    @Transactional
    public void approveStartingSpecialProducts() {
        LocalDate today = LocalDate.now();
        // 할인 시작날짜와 종료날짜가 오늘을 포함하는 상품 조회
        List<SpecialProduct> startingProducts = specialProductRepository.findAllStartingSpecialProducts(today);
        for (SpecialProduct sp : startingProducts) {
            // 할인 시작 상품 승인
            specialProductService.approve(sp.getId());
        }
    }
}
