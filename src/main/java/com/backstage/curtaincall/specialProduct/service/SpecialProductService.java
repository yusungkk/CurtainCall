package com.backstage.curtaincall.specialProduct.service;

import com.backstage.curtaincall.specialProduct.dto.SpecialProductDto;
import com.backstage.curtaincall.specialProduct.entity.SpecialProduct;
import com.backstage.curtaincall.specialProduct.repository.SpecialProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpecialProductService {

    private final SpecialProductRepository specialProductRepository;

    //전체조회
    public List<SpecialProductDto> findAllWithProduct(){
        List<SpecialProduct> specialProducts = specialProductRepository.findAllWithProduct();
        List<SpecialProductDto> specialProductDtoList = specialProducts.stream()
                                                        .map(SpecialProduct::toDto)
                                                        .toList();

        return specialProductDtoList;
    }




}
