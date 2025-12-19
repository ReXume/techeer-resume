package com.techeer.backend.api.bookmark.application.port.in;

import com.techeer.backend.api.bookmark.dto.response.BookmarkInfoResponse;

public interface GetBookmarkUseCase {

	BookmarkInfoResponse getBookmark(Long bookmarkId);

}
