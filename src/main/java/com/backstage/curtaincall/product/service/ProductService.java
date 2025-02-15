package com.backstage.curtaincall.product.service;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.image.S3Service;
import com.backstage.curtaincall.product.dto.ProductDetailRequestDto;
import com.backstage.curtaincall.product.dto.ProductRequestDto;
import com.backstage.curtaincall.product.dto.ProductResponseDto;
import com.backstage.curtaincall.product.entity.Dates;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.product.entity.ProductDetail;
import com.backstage.curtaincall.product.entity.ProductImage;
import com.backstage.curtaincall.product.repository.ProductDetailRepository;
import com.backstage.curtaincall.product.repository.ProductImageRepository;
import com.backstage.curtaincall.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final ProductImageRepository productImageRepository;
    private final S3Service s3Service;
/*

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
*/
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProducts(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size))
                .map(ProductResponseDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        Product findProduct = optionalProduct.orElseThrow(() -> new CustomException(CustomErrorCode.PRODUCT_NOT_FOUND));

        return ProductResponseDto.fromEntity(findProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductByDetailId(Long id) {
        Optional<ProductDetail> optionalProduct = productDetailRepository.findById(id);
        ProductDetail findProduct = optionalProduct.orElseThrow(() -> new CustomException(CustomErrorCode.PRODUCT_NOT_FOUND));

        return ProductResponseDto.fromEntity(findProduct.getProduct());
    }


    // 상품 등록
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto, MultipartFile file) throws IOException {
        try {
            // S3에 이미지 업로드 후 URL 가져오기
            String imageUrl = s3Service.uploadFile(file.getOriginalFilename(), file.getInputStream(), file.getSize());

            // Product 엔티티 저장
            Product product = requestDto.toEntity();
            productRepository.save(product);

            // ProductDetail 리스트 저장
            // 선택된 요일과 시간을 기반으로 시작 날짜~종료 날짜 사이의 특정 요일 찾기
            List<ProductDetail> productDetails = new ArrayList<>();
            LocalDate startDate = requestDto.getStartDate();
            LocalDate endDate = requestDto.getEndDate();

            for (ProductDetailRequestDto detailDto : requestDto.getProductDetails()) {
                LocalDate currentDate = startDate;
                DayOfWeek targetDayOfWeek = convertToDayOfWeek(detailDto.getDate());

                // 시작 날짜~종료 날짜를 while 문으로 반복하면서 선택한 요일과 일치하는 날짜만 필터링
                while (!currentDate.isAfter(endDate)) {
                    if (currentDate.getDayOfWeek() == targetDayOfWeek) {  // 공연 일정 생성
                        ProductDetail productDetail = ProductDetail.builder()
                                .product(product)
                                .dates(detailDto.getDate()) // Enum 타입 저장
                                .time(detailDto.getTime())  // 시간 저장
                                .remain(detailDto.getRemain())
                                .performanceDate(currentDate) // 실제 공연 날짜 추가
                                .build();
                        productDetails.add(productDetail);
                    }
                    currentDate = currentDate.plusDays(1); // 다음 날짜로 이동
                }
            }
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

    // 한글 요일을 Java DayOfWeek 로 변환하기 위한 메서드
    private DayOfWeek convertToDayOfWeek(Dates dateEnum) {
        return switch (dateEnum) {
            case MONDAY -> DayOfWeek.MONDAY;
            case TUESDAY -> DayOfWeek.TUESDAY;
            case WEDNESDAY -> DayOfWeek.WEDNESDAY;
            case THURSDAY -> DayOfWeek.THURSDAY;
            case FRIDAY -> DayOfWeek.FRIDAY;
            case SATURDAY -> DayOfWeek.SATURDAY;
            case SUNDAY -> DayOfWeek.SUNDAY;
        };
    }

    // 상품 수정
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

    @Transactional
    public void deleteProduct(Long productId) {
        // Product 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));

        // S3 및 DB에서 이미지 삭제
        ProductImage productImage = product.getProductImage();
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
