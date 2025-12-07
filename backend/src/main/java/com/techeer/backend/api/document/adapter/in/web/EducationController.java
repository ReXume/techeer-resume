package com.techeer.backend.api.document.adapter.in.web;

import com.techeer.backend.api.document.application.port.in.CreateEducationUseCase;
import com.techeer.backend.api.document.dto.request.EducationCreateRequest;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Operation(summary = "학력 등록", description = "새로운 학력을 등록합니다. 증명 파일과 함께 업로드하세요.")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<Long>> createEducation(
        @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
        @Valid @RequestPart("request") EducationCreateRequest request,
        @RequestPart("file") MultipartFile file
    ) {
        Long educationId = createEducationUseCase.createEducation(request, file);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(SuccessCode.EDUCATION_CREATE_SUCCESS, educationId));
    }
}
