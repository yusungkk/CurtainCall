package com.backstage.curtaincall.specialProduct.controller;

import com.backstage.curtaincall.specialProduct.dto.SpecialProductDto;
import com.backstage.curtaincall.specialProduct.service.SpecialProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/specialProduct")
@RequiredArgsConstructor
public class SpecialProductController {

    private final SpecialProductService specialProductService;

    // 전체 조회 - 관리자페이지에서 사용
    @GetMapping
    public ResponseEntity<List<SpecialProductDto>> findAll(){
        List<SpecialProductDto> dtos = specialProductService.findAllWithProduct();
        return ResponseEntity.ok(dtos);
    }

    // 생성
    @PostMapping
    public ResponseEntity<SpecialProductDto> createSpecialProduct(@RequestBody SpecialProductDto dto) {
        SpecialProductDto createdDto = specialProductService.createSpecialProduct(dto);
        return ResponseEntity.ok(createdDto);
    }

    // 수정
    @PutMapping
    public ResponseEntity<SpecialProductDto> updateSpecialProduct(@RequestBody SpecialProductDto dto) {
        SpecialProductDto updatedDto = specialProductService.update(dto);
        return ResponseEntity.ok(updatedDto);
    }

    // 삭제 (소프트 삭제)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSpecialProduct(@PathVariable Long id) {
        specialProductService.delete(id);
    }

    //카테고리 복구
    @PutMapping("restore/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void restore(@PathVariable Long id) {
        specialProductService.restore(id);
    }
}
