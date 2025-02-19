package com.backstage.curtaincall.specialProduct.service;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component
public class SpecialProductScheduler {

    private final SpecialProductService specialProductService;
    private final RedisTemplate<String, Object> redisTemplate;

    public SpecialProductScheduler(SpecialProductService specialProductService,
                                   RedisTemplate<String, Object> redisTemplate) {
        this.specialProductService = specialProductService;
        this.redisTemplate = redisTemplate;
    }

    // 매일 자정에 두 작업을 순차적으로 실행 (각각 별도의 트랜잭션)
    @Scheduled(cron = "0 0 0 * * ?")
    public void processSpecialProducts() {
        specialProductService.deleteExpiredSpecialProducts();
        specialProductService.cacheStartingSpecialProducts(redisTemplate);
    }
}

