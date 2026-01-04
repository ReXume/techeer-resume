package com.techeer.backend.api.skill.application.service;

import com.techeer.backend.api.skill.application.port.in.UpdateSkillUseCase;
import com.techeer.backend.api.skill.application.port.out.LoadSkillPort;
import com.techeer.backend.api.skill.application.port.out.SaveSkillPort;
import com.techeer.backend.api.skill.domain.Skill;
import com.techeer.backend.api.skill.dto.request.SkillUpdateRequest;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateSkillService implements UpdateSkillUseCase {

	private final LoadSkillPort loadSkillPort;

	private final SaveSkillPort saveSkillPort;

	@Override
	public void updateSkill(Long skillId, SkillUpdateRequest request) {
		Skill skill = loadSkillPort.findById(skillId)
			.orElseThrow(() -> new BusinessException(ErrorCode.SKILL_NOT_FOUND));

		// 스킬명 변경 시 중복 체크 (대소문자 구분 없이, Soft Delete된 것은 제외)
		if (request.name() != null && !request.name().equalsIgnoreCase(skill.getName())) {
			if (loadSkillPort.findByNameIgnoreCaseAndNotDeleted(request.name()).isPresent()) {
				throw new BusinessException(ErrorCode.SKILL_ALREADY_EXISTS);
			}
		}

		skill.updateName(request.name());
	}

}

