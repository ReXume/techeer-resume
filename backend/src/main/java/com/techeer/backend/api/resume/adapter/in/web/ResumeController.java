package com.techeer.backend.api.resume.adapter.in.web;

import com.techeer.backend.api.resume.application.port.in.CreateResumeUseCase;
import com.techeer.backend.api.resume.dto.request.ResumeCreateRequest;
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
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
public class ResumeController {

	private final CreateResumeUseCase createResumeUseCase;

	@PostMapping
	public ResponseEntity<ApiResponse<Long>> createResume(@Valid @RequestBody ResumeCreateRequest request) {
		Long resumeId = createResumeUseCase.createResume(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.RESUME_CREATE_SUCCESS, resumeId));
	}
}

