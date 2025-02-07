package com.backstage.curtaincall.user.dto.response;

import com.backstage.curtaincall.user.entity.RoleType;
import com.backstage.curtaincall.user.entity.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
public class UserResponse {
    private final Long id;

    private final String email;

    private final String password;

    private final String name;

    private final String phone;

    @Enumerated(EnumType.STRING)
    private final RoleType role;

    private final boolean isActive;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.isActive = user.isActive();
    }
}
