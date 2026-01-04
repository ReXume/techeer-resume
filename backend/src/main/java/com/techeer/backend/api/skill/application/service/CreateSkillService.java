package com.techeer.backend.api.skill.application.service;

import com.techeer.backend.api.skill.application.port.in.CreateSkillUseCase;
import com.techeer.backend.api.skill.application.port.out.LoadSkillPort;
import com.techeer.backend.api.skill.application.port.out.SaveSkillPort;
import com.techeer.backend.api.skill.domain.Skill;
import com.techeer.backend.api.skill.dto.request.SkillCreateRequest;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateSkillService implements CreateSkillUseCase {

	private final LoadSkillPort loadSkillPort;

	private final SaveSkillPort saveSkillPort;

	@Override
	public Long createSkill(SkillCreateRequest request) {
		// 스킬명 중복 체크 (대소문자 구분 없이, Soft Delete된 것은 제외)
		if (loadSkillPort.findByNameIgnoreCaseAndNotDeleted(request.name()).isPresent()) {
			throw new BusinessException(ErrorCode.SKILL_ALREADY_EXISTS);
		}

		Skill skill = Skill.builder()
			.name(request.name())
			.build();

		return saveSkillPort.saveSkill(skill).getId();
	}

}

