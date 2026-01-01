package com.techeer.backend.api.skill.application.port.in;

import com.techeer.backend.api.skill.dto.request.UserSkillCreateRequest;

public interface CreateUserSkillUseCase {

    Long createUserSkill(UserSkillCreateRequest request, Long userId);

}
