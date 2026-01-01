package com.techeer.backend.api.skill.application.port.out;

import com.techeer.backend.api.skill.domain.Skill;
import com.techeer.backend.api.skill.domain.UserSkill;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;

public interface LoadUserSkillPort {

    boolean existsByUserAndSkill(User user, Skill skill);

    Optional<UserSkill> findById(Long id);

}
