package com.techeer.backend.api.document.adapter.in.web;

import com.techeer.backend.api.document.application.port.in.CreateResumeUseCase;
import com.techeer.backend.api.document.application.port.in.DeleteResumeUseCase;
import com.techeer.backend.api.document.application.port.in.GetAllResumesUseCase;
import com.techeer.backend.api.document.application.port.in.GetResumeUseCase;
import com.techeer.backend.api.document.application.port.in.UpdateResumeUseCase;
import com.techeer.backend.api.document.dto.request.ResumeCreateRequest;
import com.techeer.backend.api.document.dto.request.ResumeUpdateRequest;
import com.techeer.backend.api.document.dto.response.ResumeInfoResponse;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Resume", description = "이력서 API")
@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
public class ResumeController {

	private final CreateResumeUseCase createResumeUseCase;

	private final GetResumeUseCase getResumeUseCase;

	private final GetAllResumesUseCase getAllResumesUseCase;

	private final UpdateResumeUseCase updateResumeUseCase;

	private final DeleteResumeUseCase deleteResumeUseCase;

	private final UserService userService;

	@Operation(summary = "이력서 등록", description = "새로운 이력서를 등록합니다. 파일과 함께 업로드하세요.")
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<ApiResponse<Long>> createResume(@Parameter(content = @Content(
															  mediaType = MediaType.APPLICATION_JSON_VALUE)) @Valid @RequestPart("request") ResumeCreateRequest request,
														  @RequestPart("file") MultipartFile file) {
		Long userId = userService.getLoginUser().getId();
		Long resumeId = createResumeUseCase.createResume(request, file, userId);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.RESUME_CREATE_SUCCESS, resumeId));
	}

	@Operation(summary = "이력서 단건 조회", description = "이력서 ID로 이력서 정보를 조회합니다.")
	@GetMapping("/{resumeId}")
	public ResponseEntity<ApiResponse<ResumeInfoResponse>> getResume(@PathVariable Long resumeId) {
		ResumeInfoResponse response = getResumeUseCase.getResume(resumeId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
	}

	@Operation(summary = "이력서 전체 조회", description = "현재 로그인한 사용자의 이력서 목록을 조회합니다. (Slice 페이지네이션)")
	@GetMapping
	public ResponseEntity<ApiResponse<Slice<ResumeInfoResponse>>> getAllResumes(
		@PageableDefault(size = 10) Pageable pageable) {
		Long userId = userService.getLoginUser().getId();
		Slice<ResumeInfoResponse> response = getAllResumesUseCase.getAllResumes(userId, pageable);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.RESUME_GET_SUCCESS, response));
	}

	@Operation(summary = "이력서 수정", description = "이력서를 수정합니다. 본인만 가능합니다.")
	@PutMapping("/{resumeId}")
	public ResponseEntity<ApiResponse<Void>> updateResume(@PathVariable Long resumeId,
														  @Valid @RequestBody ResumeUpdateRequest request) {
		Long userId = userService.getLoginUser().getId();
		updateResumeUseCase.updateResume(resumeId, request, userId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.RESUME_UPDATE_SUCCESS));
	}

	@Operation(summary = "이력서 삭제", description = "이력서를 삭제합니다. 본인만 가능합니다.")
	@DeleteMapping("/{resumeId}")
	public ResponseEntity<ApiResponse<Void>> deleteResume(@PathVariable Long resumeId) {
		Long userId = userService.getLoginUser().getId();
		deleteResumeUseCase.deleteResume(resumeId, userId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.RESUME_DELETE_SUCCESS));
	}

}
