package com.techeer.backend.api.skill.dto.response;

import lombok.Builder;

@Builder
public record SkillInfoResponse(Long id, String name) {
}

