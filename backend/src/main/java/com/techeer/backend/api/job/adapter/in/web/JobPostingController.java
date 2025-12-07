package com.techeer.backend.api.job.adapter.in.web;

import com.techeer.backend.api.job.application.port.in.CreateJobPostingUseCase;
import com.techeer.backend.api.job.dto.request.JobPostingCreateRequest;
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
@RequestMapping("/api/v1/job-postings")
@RequiredArgsConstructor
public class JobPostingController {

	private final CreateJobPostingUseCase createJobPostingUseCase;

	@PostMapping
	public ResponseEntity<ApiResponse<Long>> createJobPosting(@Valid @RequestBody JobPostingCreateRequest request) {
		Long jobPostingId = createJobPostingUseCase.createJobPosting(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.JOB_POSTING_CREATE_SUCCESS, jobPostingId));
	}
}

