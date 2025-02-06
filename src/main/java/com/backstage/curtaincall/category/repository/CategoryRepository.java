package com.backstage.curtaincall.category.repository;

import com.backstage.curtaincall.category.domain.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category,Long> {


    // 이름으로 Category 존재 여부 확인
    @Query("select count(*)>0 from Category c where c.name =:name")
    boolean existsByName(@Param("name") String name);

    Optional<Category> findByName(String name);

    Optional<Category> findByIdAndDeletedFalse(Long id); // 삭제되지 않은 데이터만 조회


}
