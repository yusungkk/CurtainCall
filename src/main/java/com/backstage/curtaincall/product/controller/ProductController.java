package com.backstage.curtaincall.product.controller;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.product.dto.ProductDetailResponseDto;
import com.backstage.curtaincall.product.dto.ProductRequestDto;
import com.backstage.curtaincall.product.dto.ProductResponseDto;
import com.backstage.curtaincall.product.service.ProductService;
import com.backstage.curtaincall.specialProduct.dto.SpecialProductDto;
import com.backstage.curtaincall.specialProduct.service.SpecialProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;


@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    // 특가 상품 창에서 상품 검색
    @GetMapping("/search")
    public ResponseEntity<List<SpecialProductDto>> searchProducts(@RequestParam String keyword) {
        List<SpecialProductDto> products = productService.searchProductsByKeyword(keyword);
        return ResponseEntity.ok(products);
    }

    // 상품 목록 조회 API (전체 조회)
    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponseDto>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<ProductResponseDto> products = productService.getAllProducts(page, size, sortBy, direction);

        return ResponseEntity.ok(products);
    }

    // 상품 목록 조회 API (단일 조회)
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productId) {
        ProductResponseDto response = productService.getProduct(productId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/search")
    public ResponseEntity<Page<ProductResponseDto>> getProductsByProductName(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<ProductResponseDto> response = productService.searchProductsByProductName(keyword, page, size, sortBy, direction);

        return ResponseEntity.ok(response);
    }

    // 상품 detailId로 상품 조회
    @GetMapping("/products/detail/{productDetailId}")
    public ResponseEntity<ProductResponseDto> getProductByDetailId(@PathVariable Long productDetailId) {
        ProductResponseDto response = productService.getProductByDetailId(productDetailId);

        return ResponseEntity.ok(response);
    }

    // 상품 세부 정보 조회
    @GetMapping("/products/details/{productDetailId}")
    public ResponseEntity<?> getProductDetail(@PathVariable Long productDetailId) {
        ProductDetailResponseDto response = productService.getProductDetail(productDetailId);

        return ResponseEntity.ok(response);
    }

    // 상품 등록 API
    @ResponseStatus(CREATED)
    @PostMapping("/products/new")
    public void createProduct(
            @RequestPart("product") ProductRequestDto requestDto,
            @RequestPart("image") MultipartFile image) throws IOException {

        if (Objects.isNull(image) || image.isEmpty()) {
            throw new CustomException(CustomErrorCode.EMPTY_IMAGE);
        }

        productService.createProduct(requestDto, image);
    }

    // 상품 수정 API
    @ResponseStatus(NO_CONTENT)
    @PatchMapping("/products/{productId}")
    public void updateProduct(
            @PathVariable Long productId,
            @RequestPart(value = "product", required = false) ProductRequestDto requestDto,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        productService.updateProduct(productId, requestDto, image);
    }

    // 상품 삭제 API
    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/products/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
    }

}
