package com.backstage.curtaincall.recommend.controller;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.product.dto.ProductResponseDto;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.recommend.service.UserRecommendService;
import com.backstage.curtaincall.security.JwtUtil;
import com.backstage.curtaincall.user.entity.User;
import com.backstage.curtaincall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class UserRecommendController {
    private final UserRecommendService userRecommendService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 사용자가 많이 클릭한 카테고리의 인기 상품 추천
    @GetMapping("/click")
    public ResponseEntity<List<ProductResponseDto>> getRecommendedProductsByCategory(@CookieValue(value = "jwt", required = false) String token) {
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        List<Product> recommendedProducts = userRecommendService.getRecommendedProductsByCategory(user.getId());
        List<ProductResponseDto> responseDtos = recommendedProducts.stream()
                .map(ProductResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    // 다른 사용자들이 연쇄적으로 클릭한 상품 추천
    @GetMapping("/chain")
    public ResponseEntity<List<ProductResponseDto>> getRecommendedProductsByChain(@CookieValue(value = "jwt", required = false) String token) {
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        List<Product> recommendedProducts = userRecommendService.getRecommendedProductsByChain(user.getId());
        List<ProductResponseDto> responseDtos = recommendedProducts.stream()
                .map(ProductResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }
}
