package com.techeer.backend.api.skill.dto.request;

import jakarta.validation.constraints.NotNull;

public record UserSkillCreateRequest(
	@NotNull(message = "사용자 ID는 필수입니다")
	Long userId,

	@NotNull(message = "스킬 ID는 필수입니다")
	Long skillId
) {
}

