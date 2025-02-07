package com.backstage.curtaincall.user.service;

import com.backstage.curtaincall.global.exception.CustomErrorCode;
import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.user.dto.request.UserJoinRequest;
import com.backstage.curtaincall.user.dto.request.UserUpdateRequest;
import com.backstage.curtaincall.user.entity.RoleType;
import com.backstage.curtaincall.user.entity.User;
import com.backstage.curtaincall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(rollbackFor = CustomException.class)
    public User addUser(UserJoinRequest joinRequest) {
        if(userRepository.existsByEmail(joinRequest.getEmail())) {
            throw new CustomException(CustomErrorCode.DUPLICATED_EMAIL);
        }

        User user = User.builder()
                .email(joinRequest.getEmail())
                .password(joinRequest.getPassword())
                .name(joinRequest.getName())
                .phone(joinRequest.getPhone())
                .role(RoleType.USER)
                .isActive(true)
                .build();

        return userRepository.save(user);
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
}