package com.backstage.curtaincall.user.dto.response;

import com.backstage.curtaincall.user.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private final RoleType role;
    private final String token;
}