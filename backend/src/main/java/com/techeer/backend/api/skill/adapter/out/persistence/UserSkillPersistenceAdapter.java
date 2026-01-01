package com.techeer.backend.api.skill.adapter.out.persistence;

import com.techeer.backend.api.skill.application.port.out.LoadUserSkillPort;
import com.techeer.backend.api.skill.application.port.out.SaveUserSkillPort;
import com.techeer.backend.api.skill.domain.Skill;
import com.techeer.backend.api.skill.domain.UserSkill;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserSkillPersistenceAdapter implements SaveUserSkillPort, LoadUserSkillPort {

	private final UserSkillJpaRepository userSkillJpaRepository;

	@Override
	public UserSkill saveUserSkill(UserSkill userSkill) {
		return userSkillJpaRepository.save(userSkill);
	}

	@Override
	public boolean existsByUserAndSkill(User user, Skill skill) {
		return userSkillJpaRepository.existsByUserAndSkill(user, skill);
	}

	@Override
	public Optional<UserSkill> findById(Long id) {
		// Soft Delete 적용: 삭제되지 않은 사용자 스킬만 조회
		return userSkillJpaRepository.findByIdAndNotDeleted(id);
	}

}
