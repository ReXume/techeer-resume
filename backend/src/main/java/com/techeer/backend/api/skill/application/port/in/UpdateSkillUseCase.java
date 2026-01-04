package com.techeer.backend.api.skill.application.port.in;

import com.techeer.backend.api.skill.dto.request.SkillUpdateRequest;

public interface UpdateSkillUseCase {

	void updateSkill(Long skillId, SkillUpdateRequest request);

}

