package com.backstage.curtaincall.recommend.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RelatedProductConsumer {

    private final StringRedisTemplate redisTemplate;

    @KafkaListener(topics = "related-product-events", groupId = "recommendation-group",
            containerFactory = "kafkaBatchListenerContainerFactory")
    public void consumeClickChain(List<String> messages) {

        Map<String, Map<String, Double>> clickChainMap = new HashMap<>();

        for (String message : messages) {
            String[] parts = message.split(",");
            if (parts.length != 3) continue;

            String productA = parts[1];
            String productB = parts[2];

            String redisKey = "related:clicks:" + productA;

            clickChainMap
                    .computeIfAbsent(redisKey, k -> new HashMap<>())
                    .merge(productB, 1.0, Double::sum);
        }

        // Redis에 배치 저장
        clickChainMap.forEach((key, relatedProducts) ->
                relatedProducts.forEach((productB, score) ->
                        redisTemplate.opsForZSet().incrementScore(key, productB, score)
                ));
    }
}
