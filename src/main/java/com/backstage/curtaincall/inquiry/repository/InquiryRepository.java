package com.backstage.curtaincall.inquiry.repository;

import com.backstage.curtaincall.inquiry.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    @Query("select i from Inquiry i where i.user.email = :email")
    Page<Inquiry> findAllByUser(String email, Pageable pageable);

    @Query("select i from Inquiry i where i.id = :id and i.user.email = :email")
    Optional<Inquiry> findOneByIdAndEmail(Long id, String email);
}
