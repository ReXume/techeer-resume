package com.techeer.backend.api.bookmark.application.service;

import com.techeer.backend.api.bookmark.application.port.in.GetAllBookmarksUseCase;
import com.techeer.backend.api.bookmark.application.port.out.LoadBookmarkPort;
import com.techeer.backend.api.bookmark.dto.response.BookmarkInfoResponse;
import com.techeer.backend.api.user.application.port.out.LoadUserPort;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAllBookmarksService implements GetAllBookmarksUseCase {

    private final LoadBookmarkPort loadBookmarkPort;

    private final LoadUserPort loadUserPort;

    @Override
    public Slice<BookmarkInfoResponse> getAllBookmarks(Long userId, Pageable pageable) {
        User user = loadUserPort.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return loadBookmarkPort.findAllByUser(user, pageable)
                .map(bookmark -> BookmarkInfoResponse.builder()
                        .id(bookmark.getId())
                        .jobPostingId(bookmark.getJobPosting().getId())
                        .companyName(bookmark.getJobPosting().getCompany().getName())
                        .jobTitle(bookmark.getJobPosting().getTitle())
                        .build());
    }

}
