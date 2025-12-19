package com.techeer.backend.api.skill.application.port.in;

import com.techeer.backend.api.skill.dto.response.UserSkillInfoResponse;

public interface GetUserSkillUseCase {

	UserSkillInfoResponse getUserSkill(Long userSkillId);

}
