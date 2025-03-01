package com.techeer.backend.api.user.converter;

import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.dto.response.UserInfoResponse;

public class UserConverter {

    public static UserInfoResponse ofUserInfoResponse(User user) {
        return UserInfoResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
