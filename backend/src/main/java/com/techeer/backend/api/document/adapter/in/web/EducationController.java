package com.techeer.backend.api.document.adapter.in.web;

import com.techeer.backend.api.document.application.port.in.CreateEducationUseCase;
import com.techeer.backend.api.document.application.port.in.DeleteEducationUseCase;
import com.techeer.backend.api.document.application.port.in.GetAllEducationsUseCase;
import com.techeer.backend.api.document.application.port.in.GetEducationUseCase;
import com.techeer.backend.api.document.application.port.in.UpdateEducationUseCase;
import com.techeer.backend.api.document.dto.request.EducationCreateRequest;
import com.techeer.backend.api.document.dto.request.EducationUpdateRequest;
import com.techeer.backend.api.document.dto.response.EducationInfoResponse;
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

@Tag(name = "Education", description = "학력 API")
@RestController
@RequestMapping("/api/v1/educations")
@RequiredArgsConstructor
public class EducationController {

    private final CreateEducationUseCase createEducationUseCase;

    private final UpdateEducationUseCase updateEducationUseCase;

    private final DeleteEducationUseCase deleteEducationUseCase;

    private final GetEducationUseCase getEducationUseCase;

    private final GetAllEducationsUseCase getAllEducationsUseCase;

    private final UserService userService;

    @Operation(summary = "학력 등록", description = "새로운 학력을 등록합니다. 증명 파일과 함께 업로드하세요.")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<Long>> createEducation(@Parameter(content = @Content(
                                                                     mediaType = MediaType.APPLICATION_JSON_VALUE)) @Valid @RequestPart("request") EducationCreateRequest request,
                                                             @RequestPart("file") MultipartFile file) {
        Long userId = userService.getLoginUser().getId();
        Long educationId = createEducationUseCase.createEducation(request, file, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SuccessCode.EDUCATION_CREATE_SUCCESS, educationId));
    }

    @Operation(summary = "학력 단건 조회", description = "학력 ID로 특정 학력을 조회합니다.")
    @GetMapping("/{educationId}")
    public ResponseEntity<ApiResponse<EducationInfoResponse>> getEducation(@PathVariable Long educationId) {
        EducationInfoResponse response = getEducationUseCase.getEducation(educationId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.EDUCATION_GET_SUCCESS, response));
    }

    @Operation(summary = "학력 전체 조회", description = "현재 로그인한 사용자의 학력 목록을 조회합니다. (Slice 페이지네이션)")
    @GetMapping
    public ResponseEntity<ApiResponse<Slice<EducationInfoResponse>>> getAllEducations(
            @PageableDefault(size = 10) Pageable pageable) {
        Long userId = userService.getLoginUser().getId();
        Slice<EducationInfoResponse> response = getAllEducationsUseCase.getAllEducations(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.EDUCATION_GET_SUCCESS, response));
    }

    @Operation(summary = "학력 수정", description = "학력 정보를 수정합니다.")
    @PutMapping("/{educationId}")
    public ResponseEntity<ApiResponse<Void>> updateEducation(@PathVariable Long educationId,
                                                             @Valid @RequestBody EducationUpdateRequest request) {
        Long userId = userService.getLoginUser().getId();
        updateEducationUseCase.updateEducation(educationId, request, userId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.EDUCATION_UPDATE_SUCCESS));
    }

    @Operation(summary = "학력 삭제", description = "학력을 삭제합니다. (Soft Delete)")
    @DeleteMapping("/{educationId}")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(@PathVariable Long educationId) {
        Long userId = userService.getLoginUser().getId();
        deleteEducationUseCase.deleteEducation(educationId, userId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.EDUCATION_DELETE_SUCCESS));
    }

}
