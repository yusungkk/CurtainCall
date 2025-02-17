package com.backstage.curtaincall.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private Long id;

    private Long parentId;

    @NotBlank
    @Size(max = 50, message = "이름은 1자 이상 20자 이하로 입력해주세요.")
    private String name;

    private boolean deleted;

}