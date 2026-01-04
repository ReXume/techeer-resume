package com.techeer.backend.api.skill.application.port.in;

import com.techeer.backend.api.skill.dto.response.SkillInfoResponse;

public interface GetSkillUseCase {

	SkillInfoResponse getSkill(Long skillId);

}

