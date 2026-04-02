package com.techeer.backend.api.job.adapter.in.web;

import com.techeer.backend.api.job.application.port.in.SearchJobPostingUseCase;
import com.techeer.backend.api.job.dto.request.JobSearchRequest;
import com.techeer.backend.api.job.dto.response.AutocompleteResponse;
import com.techeer.backend.api.job.dto.response.JobSearchResponse;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "JobSearch", description = "채용공고 검색 API")
@RestController
@RequestMapping("/api/v2/jobs")
@RequiredArgsConstructor
public class JobSearchController {

	private final SearchJobPostingUseCase searchJobPostingUseCase;

	@Operation(summary = "채용공고 검색", description = "전문 검색 및 필터링으로 채용공고를 검색합니다.")
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<JobSearchResponse>> search(
		@RequestParam(required = false) String query,
		@RequestParam(required = false) String position,
		@RequestParam(required = false) String experienceLevel,
		@RequestParam(required = false) List<String> skills,
		@RequestParam(required = false) String location,
		@RequestParam(required = false) String source,
		@RequestParam(required = false) Long salaryMin,
		@RequestParam(required = false) Long salaryMax,
		@RequestParam(required = false) String deadlineType,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {

		JobSearchRequest request = JobSearchRequest.builder()
			.query(query)
			.position(position)
			.experienceLevel(experienceLevel)
			.skills(skills)
			.location(location)
			.source(source)
			.salaryMin(salaryMin)
			.salaryMax(salaryMax)
			.deadlineType(deadlineType)
			.page(page)
			.size(size)
			.build();

		JobSearchResponse response = searchJobPostingUseCase.search(request);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
	}

	@Operation(summary = "채용공고 자동완성", description = "검색어 자동완성 제안을 반환합니다.")
	@GetMapping("/search/autocomplete")
	public ResponseEntity<ApiResponse<AutocompleteResponse>> autocomplete(
		@RequestParam String prefix) {
		AutocompleteResponse response = searchJobPostingUseCase.autocomplete(prefix);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
	}

	@Operation(summary = "유사 채용공고 조회", description = "지정된 채용공고와 유사한 채용공고를 반환합니다.")
	@GetMapping("/{id}/similar")
	public ResponseEntity<ApiResponse<JobSearchResponse>> findSimilar(
		@PathVariable Long id,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		JobSearchResponse response = searchJobPostingUseCase.findSimilar(id, page, size);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
	}

}
