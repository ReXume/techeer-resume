package com.techeer.backend.api.skill.application.service;

import com.techeer.backend.api.skill.application.port.in.GetSkillUseCase;
import com.techeer.backend.api.skill.application.port.out.LoadSkillPort;
import com.techeer.backend.api.skill.domain.Skill;
import com.techeer.backend.api.skill.dto.response.SkillInfoResponse;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetSkillService implements GetSkillUseCase {

	private final LoadSkillPort loadSkillPort;

	@Override
	public SkillInfoResponse getSkill(Long skillId) {
		Skill skill = loadSkillPort.findByIdAndNotDeleted(skillId)
			.orElseThrow(() -> new BusinessException(ErrorCode.SKILL_NOT_FOUND));

		return SkillInfoResponse.builder()
			.id(skill.getId())
			.name(skill.getName())
			.build();
	}

}

