package com.backstage.curtaincall.user.controller;

import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.security.JwtUtil;
import com.backstage.curtaincall.user.dto.request.UserJoinRequest;
import com.backstage.curtaincall.user.dto.request.UserLoginRequest;
import com.backstage.curtaincall.user.dto.request.UserUpdateRequest;
import com.backstage.curtaincall.user.dto.response.LoginResponse;
import com.backstage.curtaincall.user.dto.response.UserResponse;
import com.backstage.curtaincall.user.entity.RoleType;
import com.backstage.curtaincall.user.entity.User;
import com.backstage.curtaincall.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserJoinRequest joinRequest) {
        UserResponse response = userService.addUser(joinRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponse> updateUser(@CookieValue(value = "jwt", required = false) String token, @RequestBody @Valid UserUpdateRequest updateRequest) {
        String email = jwtUtil.extractEmail(token);
        User updatedUser = userService.updateUser(email, updateRequest);
        return ResponseEntity.ok(new UserResponse(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, RoleType>> login(@RequestBody @Valid UserLoginRequest loginRequest, HttpServletResponse response) {
        try {
            LoginResponse loginResponse = userService.login(loginRequest);

            Cookie cookie = new Cookie("jwt", loginResponse.getToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 30);
            response.addCookie(cookie);

            return ResponseEntity.ok(Collections.singletonMap("role", loginResponse.getRole()));
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/myPage")
    public ResponseEntity<UserResponse> getMyPage(@CookieValue(value = "jwt", required = false) String token) {
        String email = jwtUtil.extractEmail(token);
        UserResponse userResponse = userService.getUserByEmail(email);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response, @CookieValue(value = "jwt", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam String email) {
        boolean isDuplicate = userService.isEmailDuplicate(email);
        return ResponseEntity.ok(isDuplicate);
    }

    @GetMapping("/role")
    public ResponseEntity<Boolean> checkAdminRole(@CookieValue(value = "jwt", required = false) String token) {
        String email = jwtUtil.extractEmail(token);
        boolean isAdmin = userService.isAdmin(email);
        return ResponseEntity.ok(isAdmin);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserResponse>> searchUsers(@RequestParam String keyword, Pageable pageable) {
        Page<UserResponse> users = userService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(users);
    }
}