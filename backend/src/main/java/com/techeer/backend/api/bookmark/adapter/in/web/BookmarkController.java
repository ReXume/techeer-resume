package com.techeer.backend.api.bookmark.adapter.in.web;

import com.techeer.backend.api.bookmark.application.port.in.BookmarkJobPostingUseCase;
import com.techeer.backend.api.bookmark.dto.request.BookmarkCreateRequest;
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

@Tag(name = "Bookmark", description = "북마크 API")
@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

	private final BookmarkJobPostingUseCase bookmarkJobPostingUseCase;

	@Operation(summary = "채용공고 북마크", description = "채용공고를 북마크합니다.")
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> bookmarkJobPosting(@Valid @RequestBody BookmarkCreateRequest request) {
		Long bookmarkId = bookmarkJobPostingUseCase.bookmarkJobPosting(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.BOOKMARK_CREATE_SUCCESS, bookmarkId));
	}
}
