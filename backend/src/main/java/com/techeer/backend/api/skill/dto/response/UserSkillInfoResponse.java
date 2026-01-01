package com.techeer.backend.api.skill.dto.response;

import lombok.Builder;

@Builder
public record UserSkillInfoResponse(

		Long id,


		Long userId,


		Long skillId,


		String skillName

) {

}
