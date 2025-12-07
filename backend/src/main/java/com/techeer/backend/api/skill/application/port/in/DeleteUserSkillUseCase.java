package com.techeer.backend.api.skill.application.port.in;

public interface DeleteUserSkillUseCase {
    void deleteUserSkill(Long userSkillId, Long userId);
}

