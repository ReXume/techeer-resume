package com.techeer.backend.api.skill.dto.request;

import jakarta.validation.constraints.Size;

public record SkillUpdateRequest(@Size(max = 100, message = "스킬명은 100자를 초과할 수 없습니다") String name) {
}

