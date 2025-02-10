package com.backstage.curtaincall.product.controller;

import com.backstage.curtaincall.product.dto.ProductRequestDto;
import com.backstage.curtaincall.product.dto.ProductResponseDto;
import com.backstage.curtaincall.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 등록 API
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(
            @RequestPart("product") ProductRequestDto requestDto,
            @RequestPart("image") MultipartFile image) throws IOException {

        if (Objects.isNull(image) || image.isEmpty()) {
            throw new IllegalArgumentException("이미지는 필수입니다.");
        }

        log.debug("📌 받은 ProductDetails 데이터: " + requestDto.getProductDetails());

        ProductResponseDto response = productService.createProduct(requestDto, image);
        return ResponseEntity.ok(response);
    }

    // 상품 수정 API
    @PatchMapping("/{productId}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long productId,
            @RequestPart(value = "product", required = false) ProductRequestDto requestDto,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        ProductResponseDto updatedProduct = productService.updateProduct(productId, requestDto, image);

        return ResponseEntity.ok(updatedProduct);
    }

    // 상품 삭제 API
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok("상품이 성공적으로 삭제되었습니다. ID: " + productId);
    }

}
