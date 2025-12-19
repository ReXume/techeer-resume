package com.techeer.backend.api.skill.adapter.out.persistence;

import com.techeer.backend.api.skill.domain.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillJpaRepository extends JpaRepository<Skill, Long> {

}
