package com.techeer.backend.api.user.dto.request;

import com.techeer.backend.api.user.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SignUpRequest {
    @NotBlank(message = "이름 입력하세요")
    private String username;

    @NotNull(message = "권한을 입력하세요 (REGULAR, TECHEER, ADMIN)")
    private Role role;
}

