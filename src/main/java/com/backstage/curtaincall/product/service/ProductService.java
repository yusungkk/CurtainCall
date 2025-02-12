package com.backstage.curtaincall.product.service;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.image.S3Service;
import com.backstage.curtaincall.product.dto.ProductAddReq;
import com.backstage.curtaincall.product.dto.ProductDetailRequestDto;
import com.backstage.curtaincall.product.dto.ProductRequestDto;
import com.backstage.curtaincall.product.dto.ProductResponseDto;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.product.entity.ProductDetail;
import com.backstage.curtaincall.product.entity.ProductImage;
import com.backstage.curtaincall.product.repository.ProductDetailRepository;
import com.backstage.curtaincall.product.repository.ProductImageRepository;
import com.backstage.curtaincall.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ProductImageRepository productImageRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        List<Product> findProducts = productRepository.findAll();

        List<ProductResponseDto> products = new ArrayList<>();
        for (Product findProduct : findProducts) {
            ProductResponseDto productResponseDto = ProductResponseDto.fromEntity(findProduct);
            products.add(productResponseDto);
        }

        return products;
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        Product findProduct = optionalProduct.orElseThrow(() -> new CustomException(CustomErrorCode.PRODUCT_NOT_FOUND));

        return ProductResponseDto.fromEntity(findProduct);
    }


    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto, MultipartFile file) throws IOException {
        try {
            // S3에 이미지 업로드 후 URL 가져오기
            String imageUrl = s3Service.uploadFile(file.getOriginalFilename(), file.getInputStream(), file.getSize());

            // Product 엔티티 저장
            Product product = requestDto.toEntity();
            productRepository.save(product);

            // ProductDetail 리스트 저장
            List<ProductDetail> productDetails = requestDto.getProductDetails().stream()
                    .map(dto -> dto.toEntity(product))
                    .collect(Collectors.toList());
            productDetailRepository.saveAll(productDetails);

            // ProductImage 엔티티 저장
            ProductImage productImage = ProductImage.builder()
                    .imageUrl(imageUrl)
                    .product(product)
                    .build();
            productImageRepository.save(productImage);

            return ProductResponseDto.fromEntity(product);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류 발생", e);
        }

    }

    @Transactional
    public ProductResponseDto updateProduct(Long productId, ProductRequestDto requestDto, MultipartFile file) throws IOException {
        // Product 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PRODUCT_NOT_FOUND));

        // 전달된 내용 업데이트
        // 유효성 검사는 컨트롤러에서
        product.update(requestDto);

        // 이미지 업데이트
        if(file != null && !file.isEmpty()) {
            // 기존 이미지 삭제
            ProductImage productImage = product.getProductImage();
            if(productImage != null) {
                s3Service.deleteFile(productImage.getImageUrl());
                // 양방향 참조 제거
                product.updateImage(null);
                productImageRepository.delete(productImage);
                // Unique 제약 조건을 만족하기 위해 새로운 이미지 값을 저장하기 전에 미리 지워줘야 함
                productImageRepository.flush();
            }

            // 새로운 이미지 업로드
            String imageUrl = s3Service.uploadFile(file.getOriginalFilename(), file.getInputStream(), file.getSize());
            ProductImage newProductImage = ProductImage.builder()
                    .imageUrl(imageUrl)
                    .product(product)
                    .build();
            productImageRepository.save(newProductImage);
        }

        return ProductResponseDto.fromEntity(product);
    }


    public void deleteProduct(Long productId) {
        // Product 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        // S3 및 DB에서 이미지 삭제
        ProductImage productImage = productImageRepository.findByProduct(product);
        if(productImage != null) {
            s3Service.deleteFile(productImage.getImageUrl());
            productImageRepository.delete(productImage);
        }

        // ProductDetail 삭제
        productDetailRepository.deleteByProduct(product);

        // 상품 삭제
        productRepository.delete(product);
    }
}
