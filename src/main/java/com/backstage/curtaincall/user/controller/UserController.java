package com.backstage.curtaincall.user.controller;

import com.backstage.curtaincall.security.JwtUtil;
import com.backstage.curtaincall.user.dto.request.UserJoinRequest;
import com.backstage.curtaincall.user.dto.request.UserLoginRequest;
import com.backstage.curtaincall.user.dto.request.UserUpdateRequest;
import com.backstage.curtaincall.user.dto.response.UserResponse;
import com.backstage.curtaincall.user.entity.User;
import com.backstage.curtaincall.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
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
    public ResponseEntity<UserResponse> updateUser(@RequestHeader("Authorization") String token, @RequestBody @Valid UserUpdateRequest updateRequest) {
        String email = jwtUtil.extractEmail(token);
        User updatedUser = userService.updateUser(email, updateRequest);
        return ResponseEntity.ok(new UserResponse(updatedUser, null));
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
    public ResponseEntity<String> login(@RequestBody @Valid UserLoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @GetMapping("/myPage")
    public ResponseEntity<UserResponse> getMyPage(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token);
        UserResponse userResponse = userService.getUserByEmail(email);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String jwt = jwtUtil.extractEmail(token);
        if (jwt != null) {
            return ResponseEntity.ok("로그아웃 성공");
        }
        return ResponseEntity.badRequest().body("유효하지 않은 토큰");
    }
}
