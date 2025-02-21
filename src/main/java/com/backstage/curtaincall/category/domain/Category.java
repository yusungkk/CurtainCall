package com.backstage.curtaincall.category.domain;


import com.backstage.curtaincall.category.dto.CategoryDto;
import com.backstage.curtaincall.global.entity.BaseEntity;
import com.backstage.curtaincall.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name= "categories")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    @Column(name = "is_deleted")
    private boolean deleted = false;

//    @OneToMany(mappedBy = "category")
//    @JsonIgnore
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

    public void updateName(String name) {
        this.name = name;
    }


    public boolean isRootCategory() {
        return parent == null;
    }


    public CategoryDto toDto() {

        return CategoryDto.builder()
                .id(this.id)
                .parentId(this.parent != null ? this.parent.getId() : null)
                .name(this.name)
                .deleted(this.deleted)
                .build();
    }

    public void delete() {
        this.deleted = true;
    }

    public void restore() {
        this.deleted = false;
    }
}

