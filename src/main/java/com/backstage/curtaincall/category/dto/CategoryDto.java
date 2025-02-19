package com.backstage.curtaincall.category.dto;

import com.backstage.curtaincall.category.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private Long id;

    private Long parentId;

    @NotBlank
    @Size(max = 50, message = "이름은 1자 이상 50자 이하로 입력해주세요.")
    private String name;

    private boolean deleted;

    public static CategoryDto fromEntity(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .parentId(category.getParent().getId())
                .name(category.getName())
                .deleted(category.isDeleted())
                .build();
    }
}