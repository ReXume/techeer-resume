package com.techeer.backend.api.application.adapter.in.web;

import com.techeer.backend.api.application.application.port.in.ApplyJobUseCase;
import com.techeer.backend.api.application.application.port.in.CancelApplicationUseCase;
import com.techeer.backend.api.application.application.port.in.GetApplicationUseCase;
import com.techeer.backend.api.application.dto.request.ApplicationApplyRequest;
import com.techeer.backend.api.application.dto.response.ApplicationInfoResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Application", description = "지원 API")
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

	private final ApplyJobUseCase applyJobUseCase;

	private final GetApplicationUseCase getApplicationUseCase;

	private final CancelApplicationUseCase cancelApplicationUseCase;

	private final UserService userService;

	@Operation(summary = "채용공고 지원", description = "채용공고에 지원합니다.")
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> applyJob(@Valid @RequestBody ApplicationApplyRequest request) {
		Long userId = userService.getLoginUser().getId();
		Long applicationId = applyJobUseCase.applyJob(request, userId);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.APPLICATION_APPLY_SUCCESS, applicationId));
	}

	@Operation(summary = "지원 내역 단건 조회", description = "지원 ID로 지원 내역을 조회합니다.")
	@GetMapping("/{applicationId}")
	public ResponseEntity<ApiResponse<ApplicationInfoResponse>> getApplication(@PathVariable Long applicationId) {
		ApplicationInfoResponse response = getApplicationUseCase.getApplication(applicationId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
	}

	@Operation(summary = "지원 취소", description = "지원을 취소합니다. 본인만 가능합니다.")
	@DeleteMapping("/{applicationId}")
	public ResponseEntity<ApiResponse<Void>> cancelApplication(@PathVariable Long applicationId) {
		Long userId = userService.getLoginUser().getId();
		cancelApplicationUseCase.cancelApplication(applicationId, userId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.APPLICATION_CANCEL_SUCCESS));
	}

}
