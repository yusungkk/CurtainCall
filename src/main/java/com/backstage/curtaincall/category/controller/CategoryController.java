package com.backstage.curtaincall.category.controller;

import com.backstage.curtaincall.category.dto.CategoryDto;
import com.backstage.curtaincall.category.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    // 삭제 되지않는 것만 전체 조회
    @GetMapping
    public ResponseEntity<List<CategoryDto>> findAllNotDeleted(){
        List<CategoryDto> CategoryDtos = categoryService.findAllNotDeleted();

        return new ResponseEntity<>(CategoryDtos, HttpStatus.OK);
    }

    // 삭제 된 것만 전체 조회
    @GetMapping("findAllDeleted")
    public ResponseEntity<List<CategoryDto>> findAllDeleted(){
        List<CategoryDto> CategoryDtos = categoryService.findAllDeleted();

        return new ResponseEntity<>(CategoryDtos, HttpStatus.OK);
    }


    //생성
    @PostMapping
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryDto categoryDto){

        CategoryDto categoryDtoResponse = categoryService.save(categoryDto.getName(), categoryDto.getParentId());
        return new ResponseEntity<>(categoryDtoResponse, HttpStatus.OK);
    }


    //카테고리 수정
    @PutMapping
    public ResponseEntity<CategoryDto> update(@Valid @RequestBody CategoryDto categoryDto){

        CategoryDto categoryDtoResponse = categoryService.update(categoryDto.getName(), categoryDto.getId());
        return new ResponseEntity<>(categoryDtoResponse, HttpStatus.OK);
    }

    //카테고리 삭제
    @DeleteMapping("{categoryId}")
    @ResponseStatus(HttpStatus.OK) //본문이 필요하지 않으니 ResponseStatus 사용
    public void delete(@PathVariable("categoryId") Long categoryId){

        categoryService.delete(categoryId);
    }

    //카테고리 복구
    @PutMapping("restore/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public void restore(@PathVariable("categoryId") Long categoryId) {
        categoryService.restore(categoryId);
    }
}
