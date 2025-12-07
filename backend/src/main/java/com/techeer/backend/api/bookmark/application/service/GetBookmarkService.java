package com.techeer.backend.api.bookmark.application.service;

import com.techeer.backend.api.bookmark.application.port.in.GetBookmarkUseCase;
import com.techeer.backend.api.bookmark.application.port.out.LoadBookmarkPort;
import com.techeer.backend.api.bookmark.domain.Bookmark;
import com.techeer.backend.api.bookmark.dto.response.BookmarkInfoResponse;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetBookmarkService implements GetBookmarkUseCase {

    private final LoadBookmarkPort loadBookmarkPort;

    @Override
    public BookmarkInfoResponse getBookmark(Long bookmarkId) {
        Bookmark bookmark = loadBookmarkPort.findById(bookmarkId)
            .orElseThrow(() -> new BusinessException(ErrorCode.BOOKMARK_NOT_FOUND));

        return BookmarkInfoResponse.builder()
            .id(bookmark.getId())
            .jobPostingId(bookmark.getJobPosting().getId())
            .jobPostingTitle(bookmark.getJobPosting().getTitle())
            .companyName(bookmark.getJobPosting().getCompany().getName())
            .createdAt(bookmark.getCreatedAt())
            .build();
    }
}

