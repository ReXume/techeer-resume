package com.techeer.backend.api.skill.application.service;

import com.techeer.backend.api.skill.application.port.in.CreateUserSkillUseCase;
import com.techeer.backend.api.skill.application.port.out.LoadSkillPort;
import com.techeer.backend.api.skill.application.port.out.LoadUserSkillPort;
import com.techeer.backend.api.skill.application.port.out.SaveUserSkillPort;
import com.techeer.backend.api.skill.domain.Skill;
import com.techeer.backend.api.skill.domain.UserSkill;
import com.techeer.backend.api.skill.dto.request.UserSkillCreateRequest;
import com.techeer.backend.api.user.application.port.out.LoadUserPort;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateUserSkillService implements CreateUserSkillUseCase {

	private final SaveUserSkillPort saveUserSkillPort;

	private final LoadUserSkillPort loadUserSkillPort;

	private final LoadSkillPort loadSkillPort;

	private final LoadUserPort loadUserPort;

	@Override
	public Long createUserSkill(UserSkillCreateRequest request) {
		User user = loadUserPort.findById(request.userId())
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		Skill skill = loadSkillPort.findById(request.skillId())
			.orElseThrow(() -> new BusinessException(ErrorCode.SKILL_NOT_FOUND));

		// 중복 체크
		if (loadUserSkillPort.existsByUserAndSkill(user, skill)) {
			throw new BusinessException(ErrorCode.USER_SKILL_ALREADY_EXISTS);
		}

		UserSkill userSkill = UserSkill.builder()
			.user(user)
			.skill(skill)
			.build();

		return saveUserSkillPort.saveUserSkill(userSkill).getId();
	}
}

