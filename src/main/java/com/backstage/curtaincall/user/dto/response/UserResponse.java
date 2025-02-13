package com.backstage.curtaincall.user.dto.response;

import com.backstage.curtaincall.user.entity.RoleType;
import com.backstage.curtaincall.user.entity.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
public class UserResponse {
    private final String email;

    private final String password;

    private final String name;

    private final String phone;

    public UserResponse(User user, String token) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.phone = user.getPhone();
    }
}
