package com.techeer.backend.api.skill.application.service;

import com.techeer.backend.api.skill.application.port.in.GetUserSkillUseCase;
import com.techeer.backend.api.skill.application.port.out.LoadUserSkillPort;
import com.techeer.backend.api.skill.domain.UserSkill;
import com.techeer.backend.api.skill.dto.response.UserSkillInfoResponse;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserSkillService implements GetUserSkillUseCase {

	private final LoadUserSkillPort loadUserSkillPort;

	@Override
	public UserSkillInfoResponse getUserSkill(Long userSkillId) {
		UserSkill userSkill = loadUserSkillPort.findById(userSkillId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_SKILL_NOT_FOUND));

		return UserSkillInfoResponse.builder()
			.id(userSkill.getId())
			.userId(userSkill.getUser().getId())
			.skillId(userSkill.getSkill().getId())
			.skillName(userSkill.getSkill().getName())
			.build();
	}

}
