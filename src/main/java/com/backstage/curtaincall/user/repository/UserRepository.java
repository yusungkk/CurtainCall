package com.backstage.curtaincall.user.repository;

import com.backstage.curtaincall.user.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Page<User> findAll(Pageable pageable);
    Page<User> findByNameContainingOrEmailContaining(String nameKeyword, String emailKeyword, Pageable pageable);
}