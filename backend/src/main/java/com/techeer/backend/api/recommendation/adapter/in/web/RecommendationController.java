package com.techeer.backend.api.recommendation.adapter.in.web;

import com.techeer.backend.api.recommendation.application.port.in.GetRecommendationsUseCase;
import com.techeer.backend.api.recommendation.application.port.in.MarkRecommendationViewedUseCase;
import com.techeer.backend.api.recommendation.dto.response.RecommendationResponse;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Recommendation", description = "추천 채용공고 API")
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

	private final GetRecommendationsUseCase getRecommendationsUseCase;

	private final MarkRecommendationViewedUseCase markRecommendationViewedUseCase;

	private final UserService userService;

	@Operation(summary = "추천 채용공고 목록 조회", description = "현재 로그인한 사용자의 추천 채용공고를 조회합니다. (Redis 캐시 우선, DB 폴백)")
	@GetMapping
	public ResponseEntity<ApiResponse<Page<RecommendationResponse>>> getRecommendations(
		@PageableDefault(size = 10) Pageable pageable) {
		Long userId = userService.getLoginUser().getId();
		Page<RecommendationResponse> result = getRecommendationsUseCase.getRecommendations(userId, pageable);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, result));
	}

	@Operation(summary = "추천 채용공고 조회 표시", description = "추천 채용공고를 조회한 것으로 표시합니다.")
	@PutMapping("/{id}/viewed")
	public ResponseEntity<ApiResponse<Void>> markAsViewed(@PathVariable Long id) {
		markRecommendationViewedUseCase.markAsViewed(id);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK));
	}

}
