package com.backstage.curtaincall.category.service;


import com.backstage.curtaincall.category.domain.Category;
import com.backstage.curtaincall.category.dto.CategoryDto;
import com.backstage.curtaincall.category.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> findAllNotDeleted() {

        List<Category> categories = categoryRepository.findAllNotDeleted();
        List<CategoryDto> categoryDtos = categories.stream()
                .map(Category::toDto).toList();
        return categoryDtos;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> findAllDeleted() {

        List<Category> categories = categoryRepository.findAllDeleted();
        List<CategoryDto> categoryDtos = categories.stream()
                .map(Category::toDto).toList();
        return categoryDtos;
    }

    public CategoryDto save(String name, Long parentId){

        // 이스케이프 처리
        String escapedName = StringEscapeUtils.escapeHtml4(name);

        validate(escapedName);

        Category category = Category.from(escapedName);

        // 자식 카테고리인 경우 부모 설정
        if (parentId != null) {

            Category parent = categoryRepository.findByIdNotDeleted(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 ID가 없습니다."));

            // 루트 카테고리가 아닌 곳에서 카테고리를 추가하는 경우 오류 발생
            if (!parent.isRootCategory()) {
                throw new IllegalArgumentException("카테고리 추가는 루트 카테고리만 할 수 있습니다.");
            }

            parent.addChild(category);
        }

        return categoryRepository.save(category).toDto();
    }

    public CategoryDto update(String name, Long id){

        // 이스케이프 처리
        String escapedName = StringEscapeUtils.escapeHtml4(name);

        validate(escapedName);

        Category category = categoryRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID가 없습니다."));

        category.updateName(escapedName);
        return categoryRepository.save(category).toDto();
    }

    private void validate(String escapedName) {
        if (escapedName == null || escapedName.trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리 이름은 공백일 수 없습니다.");
        }

        // 이미 이름이 있다면 중복예외 발생
        if(categoryRepository.existsByName(escapedName)){
            throw new IllegalArgumentException(escapedName + "은 이미 존재하는 카테고리 이름입니다");
        }
    }


    public void delete(Long id) {
        Category category = categoryRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID가 없습니다."));

        // 부모카테고리면 자식카테고리 삭제
        if(category.isRootCategory()){
            categoryRepository.softDeleteChildren(id);
        }
        //자신 삭제
        category.delete();
    }


    public void restore(Long id) {
        Category category = categoryRepository.findByIdDeleted(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID가 없습니다."));
        if(category.isRootCategory()){
            categoryRepository.restoreChildren(id);
        }

        category.restore();
    }
}
