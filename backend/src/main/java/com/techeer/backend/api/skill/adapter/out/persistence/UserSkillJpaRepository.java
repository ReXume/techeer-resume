package com.techeer.backend.api.skill.adapter.out.persistence;

import com.techeer.backend.api.skill.domain.Skill;
import com.techeer.backend.api.skill.domain.UserSkill;
import com.techeer.backend.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSkillJpaRepository extends JpaRepository<UserSkill, Long> {
	boolean existsByUserAndSkill(User user, Skill skill);
}

