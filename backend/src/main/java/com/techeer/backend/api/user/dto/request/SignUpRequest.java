package com.techeer.backend.api.user.dto.request;

import com.techeer.backend.api.user.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignUpRequest(@NotBlank(message = "이름을 입력하세요") String name,

		@NotNull(message = "권한을 입력하세요 (REGULAR, TECHEER, ADMIN)") Role role) {
}
