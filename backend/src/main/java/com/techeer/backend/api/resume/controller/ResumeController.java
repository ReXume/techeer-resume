package com.techeer.backend.api.resume.controller;

import com.techeer.backend.api.resume.dto.request.ResumeCreateRequest;
import com.techeer.backend.api.resume.service.ResumeService;
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

	private final ResumeService resumeService;

	@PostMapping
	public ResponseEntity<ApiResponse<Long>> createResume(@Valid @RequestBody ResumeCreateRequest request) {
		Long resumeId = resumeService.createResume(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.RESUME_CREATE_SUCCESS, resumeId));
	}
}

