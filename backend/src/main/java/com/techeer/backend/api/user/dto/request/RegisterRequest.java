package com.techeer.backend.api.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank(message = "이메일을 입력하세요") @Email(message = "올바른 이메일 형식이 아닙니다") String email,

							  @NotBlank(message = "이름을 입력하세요") String username,

							  @NotBlank(message = "비밀번호를 입력하세요") @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다") @Pattern(
								  regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
								  message = "비밀번호는 영문자와 숫자를 포함해야 합니다") String password) {
}
