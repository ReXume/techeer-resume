package com.techeer.backend.util.domain;

import com.techeer.backend.api.user.domain.Role;
import com.techeer.backend.api.user.domain.SocialType;
import com.techeer.backend.api.user.domain.User;

public class UserUtils {
    public static User newInstance() {
        return User.builder()
                .email("test_user@example.com")
                .username("test_man")
                .refreshToken("test_refresh_token")
                .role(Role.REGULAR) // 실제 Role Enum 값을 확인 후 넣어주세요
                .socialType(SocialType.GOOGLE) // 실제 SocialType Enum 값을 확인 후 넣어주세요
                .build();
    }
}
