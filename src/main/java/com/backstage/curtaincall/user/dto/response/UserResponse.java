package com.backstage.curtaincall.user.dto.response;

import com.backstage.curtaincall.user.entity.RoleType;
import com.backstage.curtaincall.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

@Getter
@Builder
@AllArgsConstructor
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

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .phone(user.getPhone())
                .build();
    }

}
