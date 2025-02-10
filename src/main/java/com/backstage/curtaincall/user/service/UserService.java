package com.backstage.curtaincall.user.service;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.security.JwtUtil;
import com.backstage.curtaincall.user.dto.request.UserJoinRequest;
import com.backstage.curtaincall.user.dto.request.UserLoginRequest;
import com.backstage.curtaincall.user.dto.request.UserUpdateRequest;
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

        String token = jwtUtil.generateToken(user.getEmail());

        return new UserResponse(user, token);
    }

    @Transactional
    public User updateUser(Long id, UserUpdateRequest updateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
        user.update(updateRequest.getPassword(), updateRequest.getName(), updateRequest.getPhone());

        return user;
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    public String login(UserLoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException(CustomErrorCode.INVALID_PASSWORD);
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}