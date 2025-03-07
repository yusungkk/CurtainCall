package com.backstage.curtaincall.user.dto.response;

import com.backstage.curtaincall.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponse {
    private final long id;

    private final String email;

    private final String password;

    private final String name;

    private final String phone;

    private final boolean isActive;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.isActive = user.isActive();
    }
}
