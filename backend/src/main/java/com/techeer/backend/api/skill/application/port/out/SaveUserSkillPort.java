package com.techeer.backend.api.skill.application.port.out;

import com.techeer.backend.api.skill.domain.UserSkill;

public interface SaveUserSkillPort {

	UserSkill saveUserSkill(UserSkill userSkill);

}
