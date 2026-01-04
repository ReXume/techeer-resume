package com.techeer.backend.api.skill.adapter.in.web;

import com.techeer.backend.api.skill.application.port.in.CreateSkillUseCase;
import com.techeer.backend.api.skill.application.port.in.DeleteSkillUseCase;
import com.techeer.backend.api.skill.application.port.in.GetSkillUseCase;
import com.techeer.backend.api.skill.application.port.in.UpdateSkillUseCase;
import com.techeer.backend.api.skill.dto.request.SkillCreateRequest;
import com.techeer.backend.api.skill.dto.request.SkillUpdateRequest;
import com.techeer.backend.api.skill.dto.response.SkillInfoResponse;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Skill", description = "기술 스택 API")
@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillController {

	private final CreateSkillUseCase createSkillUseCase;

	private final GetSkillUseCase getSkillUseCase;

	private final UpdateSkillUseCase updateSkillUseCase;

	private final DeleteSkillUseCase deleteSkillUseCase;

	@Operation(summary = "기술 스택 등록", description = "새로운 기술 스택을 등록합니다.")
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> createSkill(@Valid @RequestBody SkillCreateRequest request) {
		Long skillId = createSkillUseCase.createSkill(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.SKILL_CREATE_SUCCESS, skillId));
	}

	@Operation(summary = "기술 스택 단건 조회", description = "기술 스택 ID로 기술 스택 정보를 조회합니다.")
	@GetMapping("/{skillId}")
	public ResponseEntity<ApiResponse<SkillInfoResponse>> getSkill(@PathVariable Long skillId) {
		SkillInfoResponse response = getSkillUseCase.getSkill(skillId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.SKILL_GET_SUCCESS, response));
	}

	@Operation(summary = "기술 스택 수정", description = "기술 스택 정보를 수정합니다.")
	@PutMapping("/{skillId}")
	public ResponseEntity<ApiResponse<Void>> updateSkill(@PathVariable Long skillId,
														 @Valid @RequestBody SkillUpdateRequest request) {
		updateSkillUseCase.updateSkill(skillId, request);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.SKILL_UPDATE_SUCCESS));
	}

	@Operation(summary = "기술 스택 삭제", description = "기술 스택을 삭제합니다. (Soft Delete)")
	@DeleteMapping("/{skillId}")
	public ResponseEntity<ApiResponse<Void>> deleteSkill(@PathVariable Long skillId) {
		deleteSkillUseCase.deleteSkill(skillId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.SKILL_DELETE_SUCCESS));
	}

}

