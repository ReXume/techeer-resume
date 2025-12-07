package com.techeer.backend.api.bookmark.dto.request;

import jakarta.validation.constraints.NotNull;

public record BookmarkCreateRequest(
	@NotNull(message = "채용공고 ID는 필수입니다")
	Long jobPostingId,

	@NotNull(message = "사용자 ID는 필수입니다")
	Long userId
) {
}

