package com.techeer.backend.api.skill.adapter.in.web;

import com.techeer.backend.api.skill.application.port.in.CreateUserSkillUseCase;
import com.techeer.backend.api.skill.dto.request.UserSkillCreateRequest;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserSkill", description = "스킬 API")
@RestController
@RequestMapping("/api/v1/user-skills")
@RequiredArgsConstructor
public class UserSkillController {

	private final CreateUserSkillUseCase createUserSkillUseCase;

	@Operation(summary = "스킬 등록", description = "사용자의 스킬 정보를 등록합니다.")
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> createUserSkill(@Valid @RequestBody UserSkillCreateRequest request) {
		Long userSkillId = createUserSkillUseCase.createUserSkill(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.USER_SKILL_CREATE_SUCCESS, userSkillId));
	}
}
