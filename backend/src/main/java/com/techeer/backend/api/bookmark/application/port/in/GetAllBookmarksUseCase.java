package com.techeer.backend.api.bookmark.application.port.in;

import com.techeer.backend.api.bookmark.dto.response.BookmarkInfoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GetAllBookmarksUseCase {

    Slice<BookmarkInfoResponse> getAllBookmarks(Long userId, Pageable pageable);

}
