package com.techeer.backend.api.user.converter;

import com.techeer.backend.api.user.domain.Role;
import com.techeer.backend.api.user.domain.SocialType;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.dto.response.UserInfoResponse;

public class UserConverter {
    public static User fromSignUp(String email, String username, String refreshToken, Role role,
                                  SocialType socialType) {
        return User.builder()
                .email(email)
                .username(username)
                .refreshToken(refreshToken)
                .role(role)
                .socialType(socialType)
                .build();
    }

    public static UserInfoResponse ofUserInfoResponse(User user) {
        return UserInfoResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
