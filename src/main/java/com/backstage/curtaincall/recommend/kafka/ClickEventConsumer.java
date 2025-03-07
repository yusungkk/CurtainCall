package com.backstage.curtaincall.recommend.kafka;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ClickEventConsumer {
    private final StringRedisTemplate redisTemplate;
    private final ProductRepository productRepository;

    private static final String LAST_VIEWED_PRODUCT_KEY = "user:lastViewed:";
    private static final String CATEGORY_CLICKS_KEY = "user:clicks:categories:";
    private static final String PRODUCT_CLICKS_KEY = "user:clicks:products:";


    @KafkaListener(topics = "user-click-events", groupId = "recommendation-group",
            containerFactory = "kafkaBatchListenerContainerFactory")
    public void consumeClickEvents(List<String> messages) {
        Map<String, Integer> categoryClicksMap = new HashMap<>();
        Map<String, Integer> productClicksMap = new HashMap<>();
        Map<String, String> lastViewedMap = new HashMap<>();

        for (String message : messages) {
            String[] data = message.split(",");
            Long userId = Long.parseLong(data[0]);
            Long productId = Long.parseLong(data[1]);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new CustomException(CustomErrorCode.PRODUCT_NOT_FOUND));

            Long categoryId = product.getCategory().getId();

            // 마지막 본 상품 저장
            lastViewedMap.put(LAST_VIEWED_PRODUCT_KEY + userId, productId.toString());

            // 카테고리 클릭 수 누적
            incrementClickCount(categoryClicksMap, CATEGORY_CLICKS_KEY, userId, categoryId);

            // 상품 클릭 수 누적
            incrementClickCount(productClicksMap, PRODUCT_CLICKS_KEY, userId, productId);
        }

        // Redis에 일괄 저장 (배치 처리)
        if (!lastViewedMap.isEmpty()) {
            redisTemplate.opsForValue().multiSet(lastViewedMap);
        }

        // 카테고리 클릭 수
        saveClicks(categoryClicksMap);

        // 상품 클릭 수
        saveClicks(productClicksMap);
    }

    // 클릭 카운트를 누적
    private void incrementClickCount(Map<String, Integer> clicksMap, String baseKey, Long userId, Long itemId) {
        String key = baseKey + userId + ":" + itemId;
        clicksMap.put(key, clicksMap.getOrDefault(key, 0) + 1);
    }

    // ClicksMap 별로 Redis에 저장
    private void saveClicks(Map<String, Integer> clicksMap) {
        clicksMap.forEach((fullKey, count) -> {
            String[] splitKey = fullKey.split(":");
            String redisKey = String.join(":", Arrays.copyOf(splitKey, splitKey.length - 1));
            String field = splitKey[splitKey.length - 1];
            redisTemplate.opsForHash().increment(redisKey, field, count);
        });
    }
}
