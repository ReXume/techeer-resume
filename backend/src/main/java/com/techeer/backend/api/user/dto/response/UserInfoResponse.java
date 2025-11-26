package com.techeer.backend.api.user.dto.response;

import com.techeer.backend.api.user.domain.Role;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoResponse {
    private Long userId;
    private String username;
    private String email;
    private String profileImage;
    private Role role;
}
