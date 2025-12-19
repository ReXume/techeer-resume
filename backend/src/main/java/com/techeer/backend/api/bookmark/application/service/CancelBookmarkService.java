package com.techeer.backend.api.bookmark.application.service;

import com.techeer.backend.api.bookmark.application.port.in.CancelBookmarkUseCase;
import com.techeer.backend.api.bookmark.application.port.out.LoadBookmarkPort;
import com.techeer.backend.api.bookmark.domain.Bookmark;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CancelBookmarkService implements CancelBookmarkUseCase {

	private final LoadBookmarkPort loadBookmarkPort;

	@Override
	public void cancelBookmark(Long bookmarkId, Long userId) {
		Bookmark bookmark = loadBookmarkPort.findById(bookmarkId)
			.orElseThrow(() -> new BusinessException(ErrorCode.BOOKMARK_NOT_FOUND));

		if (!bookmark.getUser().getId().equals(userId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}

		bookmark.softDelete();
	}

}
