package com.backstage.curtaincall.user.service;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.security.JwtUtil;
import com.backstage.curtaincall.user.dto.request.UserJoinRequest;
import com.backstage.curtaincall.user.dto.request.UserLoginRequest;
import com.backstage.curtaincall.user.dto.request.UserUpdateRequest;
import com.backstage.curtaincall.user.dto.response.LoginResponse;
import com.backstage.curtaincall.user.dto.response.UserResponse;
import com.backstage.curtaincall.user.entity.RoleType;
import com.backstage.curtaincall.user.entity.User;
import com.backstage.curtaincall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(rollbackFor = CustomException.class)
    public UserResponse addUser(UserJoinRequest joinRequest) {
        if(userRepository.existsByEmail(joinRequest.getEmail())) {
            throw new CustomException(CustomErrorCode.DUPLICATED_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(joinRequest.getPassword());

        User user = User.builder()
                .email(joinRequest.getEmail())
                .password(encodedPassword)
                .name(joinRequest.getName())
                .phone(joinRequest.getPhone())
                .role(RoleType.USER)
                .isActive(true)
                .build();

        userRepository.save(user);
        return new UserResponse(user);
    }

    @Transactional
    public User updateUser(String email, UserUpdateRequest updateRequest) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updateRequest.getPassword());
            user.update(encodedPassword, updateRequest.getName(), updateRequest.getPhone());
        } else {
            user.update(user.getPassword(), updateRequest.getName(), updateRequest.getPhone());
        }

        return user;
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new CustomException(CustomErrorCode.USER_NOT_FOUND);
        }

        userRepository.deleteById(id);
    }

    @Transactional
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public LoginResponse login(UserLoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException(CustomErrorCode.INVALID_PASSWORD);
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(user.getRole(), token);
    }

    @Transactional
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
        return new UserResponse(user);
    }

    @Transactional
    public boolean isAdmin(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
        return user.getRole() == RoleType.ADMIN;
    }
}