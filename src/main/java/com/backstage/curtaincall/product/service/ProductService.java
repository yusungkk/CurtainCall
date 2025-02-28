package com.backstage.curtaincall.product.service;

import com.backstage.curtaincall.category.domain.Category;
import com.backstage.curtaincall.category.repository.CategoryRepository;
import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.image.S3Service;
import com.backstage.curtaincall.product.dto.ProductDetailRequestDto;
import com.backstage.curtaincall.product.dto.ProductRequestDto;
import com.backstage.curtaincall.product.dto.ProductResponseDto;
import com.backstage.curtaincall.product.dto.*;
import com.backstage.curtaincall.product.entity.Dates;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.product.entity.ProductDetail;
import com.backstage.curtaincall.product.entity.ProductImage;
import com.backstage.curtaincall.product.repository.ProductDetailRepository;
import com.backstage.curtaincall.product.repository.ProductImageRepository;
import com.backstage.curtaincall.product.repository.ProductRepository;
import com.backstage.curtaincall.recommend.service.UserRecommendService;
import com.backstage.curtaincall.specialProduct.dto.SpecialProductDto;
import com.backstage.curtaincall.specialProduct.entity.SpecialProduct;
import com.backstage.curtaincall.specialProduct.entity.SpecialProductStatus;
import com.backstage.curtaincall.specialProduct.handler.SpecialProductDeleteHandler;
import com.backstage.curtaincall.specialProduct.service.SpecialProductService;
import com.backstage.curtaincall.specialProduct.handler.SpecialProductUpdateHandler;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final CategoryRepository categoryRepository;
    private final SpecialProductUpdateHandler specialProductUpdateHandler;
    private final SpecialProductDeleteHandler specialProductDeleteHandler;


    @Transactional(readOnly = true)
    public List<SpecialProductDto> searchProductsByKeyword(String keyword) {
        return productRepository.findByProductNameContaining(keyword)
                .stream()
                .map(SpecialProductDto::of)
                .collect(Collectors.toList());
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserRecommendService userRecommendService;

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProducts(int page, int size, String sortBy, String direction) {
        Pageable pageable = sortPage(page, size, sortBy, direction);

        return productRepository.findAll(pageable)
                .map(ProductResponseDto::fromEntity);
    }

//    @Transactional(readOnly = true)
//    public ProductResponseDto getProduct(Long id) {
//        Optional<Product> optionalProduct = productRepository.findById(id);
//        Product findProduct = optionalProduct.orElseThrow(() -> new CustomException(CustomErrorCode.PRODUCT_NOT_FOUND));
//
//        return ProductResponseDto.fromEntity(findProduct);
//    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long id) {
        //상품과 연관된거 productDetails빼고 전부 가져오기
        Product product = productRepository.findAllWithoutDetails(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PRODUCT_NOT_FOUND));

        return ProductResponseDto.of(product);
    }


    @Transactional(readOnly = true)
    public Page<ProductResponseDto> searchProducts(String keyword, String genre, int page, int size, String sortBy, String direction) {
        Pageable pageable = sortPage(page, size, sortBy, direction);

        if (genre == null) {
            return productRepository.findByProductNameContaining(keyword, pageable)
                    .map(ProductResponseDto::fromEntity);
        } else {
            if ("all".equals(genre)) {
                return productRepository.findAllNotEnd(pageable)
                        .map(ProductResponseDto::fromEntity);
            }
            return productRepository.findByCategoryName(genre, pageable)
                    .map(ProductResponseDto::fromEntity);
        }
    }

    private Pageable sortPage(int page, int size, String sortBy, String direction) {
        Pageable pageable;

        if (sortBy == null) {
            pageable = PageRequest.of(page, size);
        } else {
            Sort sort = direction.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();

            pageable = PageRequest.of(page, size, sort);
        }
        return pageable;
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductByDetailId(Long id) {
        Optional<ProductDetail> optionalProduct = productDetailRepository.findById(id);
        ProductDetail findProduct = optionalProduct.orElseThrow(() -> new CustomException(CustomErrorCode.PRODUCT_NOT_FOUND));

        return ProductResponseDto.of(findProduct.getProduct());
    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProductDetail(Long id) {
        Optional<ProductDetail> optionalProductDetail = productDetailRepository.findById(id);
        ProductDetail findProduct = optionalProductDetail.orElseThrow(() -> new CustomException(CustomErrorCode.PRODUCT_NOT_FOUND));

        return ProductDetailResponseDto.fromEntity(findProduct);
    }

    // 상품 등록
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto, MultipartFile file) throws IOException {
        try {
            // S3에 이미지 업로드 후 URL 가져오기
            String imageUrl = s3Service.uploadFile(file.getOriginalFilename(), file.getInputStream(), file.getSize());

            // 등록 시 선택된 카테고리 불러오기
            Optional<Category> optionalCategory = categoryRepository.findById(requestDto.getCategoryId());
            Category findCategory = optionalCategory.orElseThrow(() -> new RuntimeException("해당 카테고리 없음."));// Todo: 커스텀 처리

            // Product 엔티티 저장
            Product product = requestDto.toEntity();
            product.updateCategory(findCategory);
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
            throw new CustomException(CustomErrorCode.FAIL_IMAGE_UPLOAD);
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
        Optional<Category> optionalCategory = categoryRepository.findById(requestDto.getCategoryId());
        Category findCategory = optionalCategory.orElseThrow(() -> new RuntimeException("해당 카테고리 없음"));//Todo: 추후 커스텀 교체
        product.updateCategory(findCategory);
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

        //특가 상품 변경
        specialProductUpdateHandler.updateAllByProduct(productId, product);
        return ProductResponseDto.fromEntity(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        // Product 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.PRODUCT_NOT_FOUND));

        // 연관된 SpecialProduct 삭제
        specialProductDeleteHandler.deleteAllByProduct(productId);

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
