package com.techeer.backend.api.skill.adapter.out.persistence;

import com.techeer.backend.api.skill.application.port.out.LoadSkillPort;
import com.techeer.backend.api.skill.domain.Skill;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SkillPersistenceAdapter implements LoadSkillPort {

	private final SkillJpaRepository skillJpaRepository;

	@Override
	public Optional<Skill> findById(Long id) {
		return skillJpaRepository.findById(id);
	}
}

