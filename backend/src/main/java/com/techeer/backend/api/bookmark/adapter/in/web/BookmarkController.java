package com.techeer.backend.api.bookmark.adapter.in.web;

import com.techeer.backend.api.bookmark.application.port.in.BookmarkJobPostingUseCase;
import com.techeer.backend.api.bookmark.application.port.in.CancelBookmarkUseCase;
import com.techeer.backend.api.bookmark.application.port.in.GetAllBookmarksUseCase;
import com.techeer.backend.api.bookmark.application.port.in.GetBookmarkUseCase;
import com.techeer.backend.api.bookmark.dto.request.BookmarkCreateRequest;
import com.techeer.backend.api.bookmark.dto.response.BookmarkInfoResponse;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
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

@Tag(name = "Bookmark", description = "북마크 API")
@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

	private final BookmarkJobPostingUseCase bookmarkJobPostingUseCase;

	private final GetBookmarkUseCase getBookmarkUseCase;

	private final GetAllBookmarksUseCase getAllBookmarksUseCase;

	private final CancelBookmarkUseCase cancelBookmarkUseCase;

	private final UserService userService;

	@Operation(summary = "채용공고 북마크", description = "채용공고를 북마크합니다.")
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> bookmarkJobPosting(@Valid @RequestBody BookmarkCreateRequest request) {
		Long userId = userService.getLoginUser().getId();
		Long bookmarkId = bookmarkJobPostingUseCase.bookmarkJobPosting(request, userId);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.BOOKMARK_CREATE_SUCCESS, bookmarkId));
	}

	@Operation(summary = "북마크 단건 조회", description = "북마크 ID로 북마크 정보를 조회합니다.")
	@GetMapping("/{bookmarkId}")
	public ResponseEntity<ApiResponse<BookmarkInfoResponse>> getBookmark(@PathVariable Long bookmarkId) {
		BookmarkInfoResponse response = getBookmarkUseCase.getBookmark(bookmarkId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
	}

	@Operation(summary = "북마크 전체 조회", description = "현재 로그인한 사용자의 북마크 목록을 조회합니다. (Slice 페이지네이션)")
	@GetMapping
	public ResponseEntity<ApiResponse<Slice<BookmarkInfoResponse>>> getAllBookmarks(
			@PageableDefault(size = 10) Pageable pageable) {
		Long userId = userService.getLoginUser().getId();
		Slice<BookmarkInfoResponse> response = getAllBookmarksUseCase.getAllBookmarks(userId, pageable);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.BOOKMARK_GET_SUCCESS, response));
	}

	@Operation(summary = "북마크 취소", description = "북마크를 취소합니다. 본인만 가능합니다.")
	@DeleteMapping("/{bookmarkId}")
	public ResponseEntity<ApiResponse<Void>> cancelBookmark(@PathVariable Long bookmarkId) {
		Long userId = userService.getLoginUser().getId();
		cancelBookmarkUseCase.cancelBookmark(bookmarkId, userId);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.BOOKMARK_CANCEL_SUCCESS));
	}

}
