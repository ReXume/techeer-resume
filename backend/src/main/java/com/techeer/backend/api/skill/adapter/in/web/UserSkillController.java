package com.techeer.backend.api.skill.adapter.in.web;

import com.techeer.backend.api.skill.application.port.in.CreateUserSkillUseCase;
import com.techeer.backend.api.skill.application.port.in.DeleteUserSkillUseCase;
import com.techeer.backend.api.skill.application.port.in.GetUserSkillUseCase;
import com.techeer.backend.api.skill.dto.request.UserSkillCreateRequest;
import com.techeer.backend.api.skill.dto.response.UserSkillInfoResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserSkill", description = "스킬 API")
@RestController
@RequestMapping("/api/v1/user-skills")
@RequiredArgsConstructor
public class UserSkillController {

    private final CreateUserSkillUseCase createUserSkillUseCase;
    private final GetUserSkillUseCase getUserSkillUseCase;
    private final DeleteUserSkillUseCase deleteUserSkillUseCase;

    @Operation(summary = "스킬 등록", description = "사용자의 스킬 정보를 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createUserSkill(@Valid @RequestBody UserSkillCreateRequest request) {
        Long userSkillId = createUserSkillUseCase.createUserSkill(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(SuccessCode.USER_SKILL_CREATE_SUCCESS, userSkillId));
    }

    @Operation(summary = "스킬 단건 조회", description = "스킬 ID로 스킬 정보를 조회합니다.")
    @GetMapping("/{userSkillId}")
    public ResponseEntity<ApiResponse<UserSkillInfoResponse>> getUserSkill(@PathVariable Long userSkillId) {
        UserSkillInfoResponse response = getUserSkillUseCase.getUserSkill(userSkillId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @Operation(summary = "스킬 삭제", description = "스킬 정보를 삭제합니다. 본인만 가능합니다.")
    @DeleteMapping("/{userSkillId}")
    public ResponseEntity<ApiResponse<Void>> deleteUserSkill(
        @PathVariable Long userSkillId,
        @RequestParam Long userId
    ) {
        deleteUserSkillUseCase.deleteUserSkill(userSkillId, userId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.USER_SKILL_DELETE_SUCCESS));
    }
}
