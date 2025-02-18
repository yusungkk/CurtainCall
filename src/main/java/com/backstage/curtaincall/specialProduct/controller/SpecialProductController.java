package com.backstage.curtaincall.specialProduct.controller;

import com.backstage.curtaincall.specialProduct.dto.SpecialProductDto;
import com.backstage.curtaincall.specialProduct.service.SpecialProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/specialProduct")
@RequiredArgsConstructor
public class SpecialProductController {

    private final SpecialProductService specialProductService;

    // 전체 조회
    @GetMapping
    public List<SpecialProductDto> findAll() {
        return specialProductService.findAll();
    }

    // 삭제 된 것만 전체 조회
    @GetMapping("findAllDeleted")
    public List<SpecialProductDto> findAllDeleted(){
        return specialProductService.findAllDeleted();
    }

    // 단건 조회
    @GetMapping("{id}")
    public SpecialProductDto findByIdWithProduct(@PathVariable Long id){
        return specialProductService.findByIdWithProduct(id);
    }

    // 생성
    @PostMapping
    public SpecialProductDto save(@RequestBody SpecialProductDto dto) {
        return specialProductService.save(dto);
    }

    // 수정
    @PutMapping
    public SpecialProductDto update(@RequestBody SpecialProductDto dto) {
        return specialProductService.update(dto);
    }

    // 삭제 (소프트 삭제)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        specialProductService.delete(id);
    }

    // 복구
    @PutMapping("/restore/{id}")
    public void restore(@PathVariable Long id) {
        specialProductService.restore(id);
    }
}
