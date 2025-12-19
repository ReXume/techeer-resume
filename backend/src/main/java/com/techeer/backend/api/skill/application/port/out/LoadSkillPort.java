package com.techeer.backend.api.skill.application.port.out;

import com.techeer.backend.api.skill.domain.Skill;
import java.util.Optional;

public interface LoadSkillPort {

	Optional<Skill> findById(Long id);

}
