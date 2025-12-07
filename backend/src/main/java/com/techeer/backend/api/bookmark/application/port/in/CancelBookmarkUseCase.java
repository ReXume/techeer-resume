package com.techeer.backend.api.bookmark.application.port.in;

public interface CancelBookmarkUseCase {
    void cancelBookmark(Long bookmarkId, Long userId);
}

