package com.techeer.backend.api.user.dto.response;

import com.techeer.backend.api.user.domain.Role;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInfoResponse {
    private String username;
    private String email;
    private Role role;
}
