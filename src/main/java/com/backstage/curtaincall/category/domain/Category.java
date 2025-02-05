package com.backstage.curtaincall.category.domain;


import com.backstage.curtaincall.category.dto.CategoryDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "categories")
@EntityListeners(AuditingEntityListener.class)
public class Category {

    @Id
    @Column(name="category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<Category> children = new ArrayList<>();

// 양방향 참조 필요할 때 주석 풀기
//    @OneToMany(mappedBy = "category")
//    private List<Product> products = new ArrayList<>();

    private Category(String name) {
        this.name = name;
    }

    public static Category from(String name){
        return new Category(name);
    }

    public void addChild(Category child){
        child.setParent(this);
        this.children.add(child);
    }

    private void setParent(Category parent){
        this.parent = parent;
    }

    public void update(String name) {
        this.name = name;
    }

    public boolean isNotRootCategory() {
        return parent != null;
    }

    public CategoryDto toDto() {

        return CategoryDto.builder()
                .id(this.id)
                .parentId(this.parent != null ? this.parent.getId() : null)
                .name(this.name)
                .build();
    }
}

