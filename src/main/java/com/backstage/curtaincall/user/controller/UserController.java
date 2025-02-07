package com.backstage.curtaincall.user.controller;

import com.backstage.curtaincall.user.dto.request.UserJoinRequest;
import com.backstage.curtaincall.user.dto.request.UserUpdateRequest;
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

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid UserJoinRequest joinRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(joinRequest));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest updateRequest) {
        User updatedUser = userService.updateUser(id, updateRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
}
