package com.techeer.backend.api.bookmark.application.port.in;

import com.techeer.backend.api.bookmark.dto.request.BookmarkCreateRequest;

public interface BookmarkJobPostingUseCase {

	Long bookmarkJobPosting(BookmarkCreateRequest request, Long userId);

}
