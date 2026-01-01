package com.techeer.backend.api.career.adapter.in.web;

import com.techeer.backend.api.career.application.port.in.CreateUserCareerUseCase;
import com.techeer.backend.api.career.application.port.in.DeleteUserCareerUseCase;
import com.techeer.backend.api.career.application.port.in.GetUserCareerUseCase;
import com.techeer.backend.api.career.application.port.in.UpdateUserCareerUseCase;
import com.techeer.backend.api.career.dto.request.UserCareerCreateRequest;
import com.techeer.backend.api.career.dto.request.UserCareerUpdateRequest;
import com.techeer.backend.api.career.dto.response.UserCareerInfoResponse;
import com.techeer.backend.api.user.service.UserService;
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

@Tag(name = "UserCareer", description = "경력 API")
@RestController
@RequestMapping("/api/v1/user-careers")
@RequiredArgsConstructor
public class UserCareerController {

	private final CreateUserCareerUseCase createUserCareerUseCase;

	private final GetUserCareerUseCase getUserCareerUseCase;

	private final UpdateUserCareerUseCase updateUserCareerUseCase;

	private final DeleteUserCareerUseCase deleteUserCareerUseCase;

	private final UserService userService;

	@Operation(summary = "경력 등록", description = "사용자의 경력 정보를 등록합니다.")
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> createUserCareer(@Valid @RequestBody UserCareerCreateRequest request) {
		Long userId = userService.getLoginUser().getId();
		Long careerId = createUserCareerUseCase.createUserCareer(request, userId);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.USER_CAREER_CREATE_SUCCESS, careerId));
	}

	@Operation(summary = "경력 단건 조회", description = "경력 ID로 경력 정보를 조회합니다.")
	@GetMapping("/{careerId}")
	public ResponseEntity<ApiResponse<UserCareerInfoResponse>> getUserCareer(@PathVariable Long careerId) {
		UserCareerInfoResponse response = getUserCareerUseCase.getUserCareer(careerId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
	}

	@Operation(summary = "경력 수정", description = "경력 정보를 수정합니다. 본인만 가능합니다.")
	@PutMapping("/{careerId}")
	public ResponseEntity<ApiResponse<Void>> updateUserCareer(@PathVariable Long careerId,
															  @Valid @RequestBody UserCareerUpdateRequest request) {
		Long userId = userService.getLoginUser().getId();
		updateUserCareerUseCase.updateUserCareer(careerId, request, userId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.USER_CAREER_UPDATE_SUCCESS));
	}

	@Operation(summary = "경력 삭제", description = "경력 정보를 삭제합니다. 본인만 가능합니다.")
	@DeleteMapping("/{careerId}")
	public ResponseEntity<ApiResponse<Void>> deleteUserCareer(@PathVariable Long careerId) {
		Long userId = userService.getLoginUser().getId();
		deleteUserCareerUseCase.deleteUserCareer(careerId, userId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.USER_CAREER_DELETE_SUCCESS));
	}

}
