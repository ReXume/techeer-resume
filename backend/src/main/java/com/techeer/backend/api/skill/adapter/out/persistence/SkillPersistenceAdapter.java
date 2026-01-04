package com.techeer.backend.api.skill.adapter.out.persistence;

import com.techeer.backend.api.skill.application.port.out.LoadSkillPort;
import com.techeer.backend.api.skill.application.port.out.SaveSkillPort;
import com.techeer.backend.api.skill.domain.Skill;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SkillPersistenceAdapter implements LoadSkillPort, SaveSkillPort {

	private final SkillJpaRepository skillJpaRepository;

	@Override
	public Optional<Skill> findById(Long id) {
		return skillJpaRepository.findById(id);
	}

	@Override
	public Optional<Skill> findByName(String name) {
		return skillJpaRepository.findByName(name);
	}

	@Override
	public Optional<Skill> findByNameIgnoreCase(String name) {
		return skillJpaRepository.findByNameIgnoreCase(name);
	}

	@Override
	public Optional<Skill> findByIdAndNotDeleted(Long id) {
		return skillJpaRepository.findByIdAndNotDeleted(id);
	}

	@Override
	public Optional<Skill> findByNameIgnoreCaseAndNotDeleted(String name) {
		return skillJpaRepository.findByNameIgnoreCaseAndNotDeleted(name);
	}

	@Override
	public Skill saveSkill(Skill skill) {
		return skillJpaRepository.save(skill);
	}

}
