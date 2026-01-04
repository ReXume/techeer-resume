package com.techeer.backend.api.skill.application.port.out;

import com.techeer.backend.api.skill.domain.Skill;

public interface SaveSkillPort {

	Skill saveSkill(Skill skill);

}

