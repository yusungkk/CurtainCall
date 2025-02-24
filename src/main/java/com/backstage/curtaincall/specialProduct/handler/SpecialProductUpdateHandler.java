package com.backstage.curtaincall.specialProduct.handler;

import static com.backstage.curtaincall.global.exception.CustomErrorCode.CANNOT_UPDATE_DELETED_SPECIAL_PRODUCT;

import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.product.entity.Product;
import com.backstage.curtaincall.specialProduct.dto.SpecialProductDto;
import com.backstage.curtaincall.specialProduct.entity.SpecialProduct;
import com.backstage.curtaincall.specialProduct.entity.SpecialProductStatus;
import com.backstage.curtaincall.specialProduct.service.SpecialProductService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpecialProductUpdateHandler {

    private final SpecialProductService specialProductService;

    @Transactional
    public void update(SpecialProductDto dto) {

        // 1. 유효성 검사 수행
        specialProductService.validate(dto);

        // 2. 특가상품 조회
        SpecialProduct sp = specialProductService.findById(dto.getSpecialProductId());

        if (dto.getStatus() == SpecialProductStatus.ACTIVE) {
            // 캐시 반영하여 변경
            specialProductService.updateWithCache(sp,dto);
        } else if (dto.getStatus() == SpecialProductStatus.UPCOMING) {
            // 캐시를 조회하지 않고 변경
            specialProductService.updateWithOutCache(sp, dto);
        } else {
            throw new CustomException(CANNOT_UPDATE_DELETED_SPECIAL_PRODUCT);
        }
    }


    //  특정 상품(Product)에 연결된 모든 특가 상품을 업데이트
    @Transactional
    public void updateAllByProduct(Long productId, Product updatedProduct) {
        List<SpecialProduct> specialProducts = specialProductService.findAllByProductId(productId);

        for (SpecialProduct sp : specialProducts) {
            SpecialProductDto updatedDto = sp.toUpdatedDto(updatedProduct);
            specialProductService.validateOverDate(updatedDto);

            if (sp.getStatus() == SpecialProductStatus.ACTIVE) {
                // 캐시 반영하여 변경
                specialProductService.updateWithCache(sp, updatedDto);
            } else if (sp.getStatus() == SpecialProductStatus.UPCOMING) {
                // 캐시를 조회하지 않고 변경
                specialProductService.updateWithOutCache(sp, updatedDto);
            }
        }
    }
}
