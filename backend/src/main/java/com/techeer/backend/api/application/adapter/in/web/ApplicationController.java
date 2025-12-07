package com.techeer.backend.api.application.adapter.in.web;

import com.techeer.backend.api.application.application.port.in.ApplyJobUseCase;
import com.techeer.backend.api.application.dto.request.ApplicationApplyRequest;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

	private final ApplyJobUseCase applyJobUseCase;

	@PostMapping
	public ResponseEntity<ApiResponse<Long>> applyJob(@Valid @RequestBody ApplicationApplyRequest request) {
		Long applicationId = applyJobUseCase.applyJob(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.APPLICATION_APPLY_SUCCESS, applicationId));
	}
}

