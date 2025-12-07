package com.techeer.backend.api.company.adapter.in.web;

import com.techeer.backend.api.company.application.port.in.GetCompanyLikeUseCase;
import com.techeer.backend.api.company.application.port.in.LikeCompanyUseCase;
import com.techeer.backend.api.company.application.port.in.UnlikeCompanyUseCase;
import com.techeer.backend.api.company.dto.request.CompanyLikeCreateRequest;
import com.techeer.backend.api.company.dto.response.CompanyLikeInfoResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CompanyLike", description = "기업 좋아요 API")
@RestController
@RequestMapping("/api/v1/company-likes")
@RequiredArgsConstructor
public class CompanyLikeController {

    private final LikeCompanyUseCase likeCompanyUseCase;
    private final GetCompanyLikeUseCase getCompanyLikeUseCase;
    private final UnlikeCompanyUseCase unlikeCompanyUseCase;

    @Operation(summary = "기업 좋아요", description = "기업에 좋아요를 표시합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> likeCompany(@Valid @RequestBody CompanyLikeCreateRequest request) {
        Long likeId = likeCompanyUseCase.likeCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(SuccessCode.COMPANY_LIKE_CREATE_SUCCESS, likeId));
    }

    @Operation(summary = "좋아요 단건 조회", description = "좋아요 ID로 좋아요 정보를 조회합니다.")
    @GetMapping("/{companyLikeId}")
    public ResponseEntity<ApiResponse<CompanyLikeInfoResponse>> getCompanyLike(@PathVariable Long companyLikeId) {
        CompanyLikeInfoResponse response = getCompanyLikeUseCase.getCompanyLike(companyLikeId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @Operation(summary = "좋아요 취소", description = "좋아요를 취소합니다. 본인만 가능합니다.")
    @DeleteMapping("/{companyLikeId}")
    public ResponseEntity<ApiResponse<Void>> unlikeCompany(
        @PathVariable Long companyLikeId,
        @RequestParam Long userId
    ) {
        unlikeCompanyUseCase.unlikeCompany(companyLikeId, userId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.COMPANY_LIKE_CANCEL_SUCCESS));
    }
}
