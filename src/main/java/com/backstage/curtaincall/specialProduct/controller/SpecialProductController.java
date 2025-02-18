package com.backstage.curtaincall.specialProduct.controller;

import com.backstage.curtaincall.specialProduct.dto.SpecialProductDto;
import com.backstage.curtaincall.specialProduct.dto.SpecialProductResponseDto;
import com.backstage.curtaincall.specialProduct.service.SpecialProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/specialProduct")
@RequiredArgsConstructor
public class SpecialProductController {

    private final SpecialProductService specialProductService;

    //전체 조회 - 관리자페이지에서 사용
    @GetMapping
    public List<SpecialProductDto> findAll(){

        return specialProductService.findAllWithProduct();
    }

    //단일 조회 - 할 일이 있나?
//    @GetMapping("/{id}")
//    public SpecialProductDto findById(@PathVariable Long id){
//
//    }

    //생성
    //수정

    //시간 다되면 자동 삭제

}
