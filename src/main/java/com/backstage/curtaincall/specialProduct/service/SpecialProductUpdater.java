package com.backstage.curtaincall.specialProduct.service;

import static com.backstage.curtaincall.global.exception.CustomErrorCode.CANNOT_UPDATE_DELETED_SPECIAL_PRODUCT;

import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.specialProduct.dto.SpecialProductDto;
import com.backstage.curtaincall.specialProduct.entity.SpecialProductStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpecialProductUpdater {

    private final SpecialProductService specialProductService;

    @Transactional
    public void update(SpecialProductDto dto) {
        if (dto.getStatus() == SpecialProductStatus.ACTIVE) {
            specialProductService.updateWithCache(dto);
        } else if (dto.getStatus() == SpecialProductStatus.UPCOMING) {
            specialProductService.updateWithOutCache(dto);
        } else {
            throw new CustomException(CANNOT_UPDATE_DELETED_SPECIAL_PRODUCT);
        }
    }
}
