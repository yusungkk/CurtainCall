package com.backstage.curtaincall.specialProduct.service;


import com.backstage.curtaincall.specialProduct.dto.SpecialProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SpecialProductScheduler {

    private final SchedulerService schedulerService;

    // 매일 자정에 두 작업을 순차적으로 실행 (각각 별도의 트랜잭션)
    @Scheduled(cron = "40 25 13 * * ?")
    public void processSpecialProducts() {

        // 매일 해당 시간에 할인 종료 날짜가 오늘 이전인 상품을 DB와 redis에서 삭제
        schedulerService.deleteExpiredSpecialProducts();

        // 매일 해당 시간에 할인 기간이 오늘을 포함하는 상품을 DB와 redis에 생성
        schedulerService.approveStartingSpecialProducts();
    }
}

