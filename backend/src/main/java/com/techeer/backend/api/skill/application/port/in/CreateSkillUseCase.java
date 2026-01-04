package com.techeer.backend.api.skill.application.port.in;

import com.techeer.backend.api.skill.dto.request.SkillCreateRequest;

public interface CreateSkillUseCase {

	Long createSkill(SkillCreateRequest request);

}

