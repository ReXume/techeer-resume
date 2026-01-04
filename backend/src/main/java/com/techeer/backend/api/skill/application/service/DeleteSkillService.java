package com.techeer.backend.api.skill.application.service;

import com.techeer.backend.api.skill.application.port.in.DeleteSkillUseCase;
import com.techeer.backend.api.skill.application.port.out.LoadSkillPort;
import com.techeer.backend.api.skill.domain.Skill;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteSkillService implements DeleteSkillUseCase {

	private final LoadSkillPort loadSkillPort;

	@Override
	public void deleteSkill(Long skillId) {
		Skill skill = loadSkillPort.findByIdAndNotDeleted(skillId)
			.orElseThrow(() -> new BusinessException(ErrorCode.SKILL_NOT_FOUND));

		// Soft Delete: BaseEntity의 softDelete() 메서드 사용
		skill.softDelete();
	}

}

