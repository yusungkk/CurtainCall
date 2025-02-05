package com.backstage.curtaincall.inquiry.repository;

import com.backstage.curtaincall.inquiry.entity.Faq;
import com.backstage.curtaincall.inquiry.entity.FaqType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {

    @Query("select f from Faq f where f.type = :type")
    Page<Faq> findAllByFaqType(FaqType type, Pageable pageable);
}
