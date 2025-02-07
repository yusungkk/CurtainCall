package com.backstage.curtaincall.user.repository;

import com.backstage.curtaincall.user.entity.ProviderType;
import com.backstage.curtaincall.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findBySocialIdAndProvider(String socialId, ProviderType provider);
}