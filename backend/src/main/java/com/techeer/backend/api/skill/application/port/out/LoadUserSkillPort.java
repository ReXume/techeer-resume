package com.techeer.backend.api.skill.application.port.out;

import com.techeer.backend.api.skill.domain.Skill;
import com.techeer.backend.api.user.domain.User;

public interface LoadUserSkillPort {
	boolean existsByUserAndSkill(User user, Skill skill);
}

