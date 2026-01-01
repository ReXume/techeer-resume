package com.techeer.backend.api.skill.application.service;

import com.techeer.backend.api.skill.application.port.in.DeleteUserSkillUseCase;
import com.techeer.backend.api.skill.application.port.out.LoadUserSkillPort;
import com.techeer.backend.api.skill.domain.UserSkill;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteUserSkillService implements DeleteUserSkillUseCase {

    private final LoadUserSkillPort loadUserSkillPort;

    @Override
    public void deleteUserSkill(Long userSkillId, Long userId) {
        UserSkill userSkill = loadUserSkillPort.findById(userSkillId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_SKILL_NOT_FOUND));

        if (!userSkill.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        userSkill.softDelete();
    }

}
