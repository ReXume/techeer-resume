package com.techeer.backend.api.company.adapter.in.web;

import com.techeer.backend.api.company.application.port.in.LikeCompanyUseCase;
import com.techeer.backend.api.company.dto.request.CompanyLikeCreateRequest;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CompanyLike", description = "기업 좋아요 API")
@RestController
@RequestMapping("/api/v1/company-likes")
@RequiredArgsConstructor
public class CompanyLikeController {

	private final LikeCompanyUseCase likeCompanyUseCase;

	@Operation(summary = "기업 좋아요", description = "기업에 좋아요를 표시합니다.")
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> likeCompany(@Valid @RequestBody CompanyLikeCreateRequest request) {
		Long likeId = likeCompanyUseCase.likeCompany(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.COMPANY_LIKE_CREATE_SUCCESS, likeId));
	}
}
