package com.techeer.backend.api.job.adapter.in.web;

import com.techeer.backend.api.job.application.port.in.CreateJobPostingUseCase;
import com.techeer.backend.api.job.application.port.in.DeleteJobPostingUseCase;
import com.techeer.backend.api.job.application.port.in.GetJobPostingUseCase;
import com.techeer.backend.api.job.application.port.in.UpdateJobPostingUseCase;
import com.techeer.backend.api.job.dto.request.JobPostingCreateRequest;
import com.techeer.backend.api.job.dto.request.JobPostingUpdateRequest;
import com.techeer.backend.api.job.dto.response.JobPostingInfoResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "JobPosting", description = "채용공고 API")
@RestController
@RequestMapping("/api/v1/job-postings")
@RequiredArgsConstructor
public class JobPostingController {

	private final CreateJobPostingUseCase createJobPostingUseCase;

	private final GetJobPostingUseCase getJobPostingUseCase;

	private final UpdateJobPostingUseCase updateJobPostingUseCase;

	private final DeleteJobPostingUseCase deleteJobPostingUseCase;

	private final UserService userService;

	@Operation(summary = "채용공고 등록", description = "새로운 채용공고를 등록합니다. 기업 관리자 권한이 필요합니다.")
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> createJobPosting(@Valid @RequestBody JobPostingCreateRequest request) {
		Long userId = userService.getLoginUser().getId();
		Long jobPostingId = createJobPostingUseCase.createJobPosting(request, userId);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.JOB_POSTING_CREATE_SUCCESS, jobPostingId));
	}

	@Operation(summary = "채용공고 단건 조회", description = "채용공고 ID로 정보를 조회합니다.")
	@GetMapping("/{jobPostingId}")
	public ResponseEntity<ApiResponse<JobPostingInfoResponse>> getJobPosting(@PathVariable Long jobPostingId) {
		JobPostingInfoResponse response = getJobPostingUseCase.getJobPosting(jobPostingId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
	}

	@Operation(summary = "채용공고 수정", description = "채용공고를 수정합니다. 기업 관리자 권한이 필요합니다.")
	@PutMapping("/{jobPostingId}")
	public ResponseEntity<ApiResponse<Void>> updateJobPosting(@PathVariable Long jobPostingId,
			@Valid @RequestBody JobPostingUpdateRequest request) {
		Long userId = userService.getLoginUser().getId();
		updateJobPostingUseCase.updateJobPosting(jobPostingId, request, userId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.JOB_POSTING_UPDATE_SUCCESS));
	}

	@Operation(summary = "채용공고 삭제", description = "채용공고를 삭제합니다. 기업 관리자 권한이 필요합니다.")
	@DeleteMapping("/{jobPostingId}")
	public ResponseEntity<ApiResponse<Void>> deleteJobPosting(@PathVariable Long jobPostingId) {
		Long userId = userService.getLoginUser().getId();
		deleteJobPostingUseCase.deleteJobPosting(jobPostingId, userId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.JOB_POSTING_DELETE_SUCCESS));
	}

}
