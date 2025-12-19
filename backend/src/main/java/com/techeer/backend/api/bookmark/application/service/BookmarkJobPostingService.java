package com.techeer.backend.api.bookmark.application.service;

import com.techeer.backend.api.bookmark.application.port.in.BookmarkJobPostingUseCase;
import com.techeer.backend.api.bookmark.application.port.out.LoadBookmarkPort;
import com.techeer.backend.api.bookmark.application.port.out.SaveBookmarkPort;
import com.techeer.backend.api.bookmark.domain.Bookmark;
import com.techeer.backend.api.bookmark.dto.request.BookmarkCreateRequest;
import com.techeer.backend.api.job.application.port.out.LoadJobPostingPort;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.user.application.port.out.LoadUserPort;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkJobPostingService implements BookmarkJobPostingUseCase {

	private final SaveBookmarkPort saveBookmarkPort;

	private final LoadBookmarkPort loadBookmarkPort;

	private final LoadJobPostingPort loadJobPostingPort;

	private final LoadUserPort loadUserPort;

	@Override
	public Long bookmarkJobPosting(BookmarkCreateRequest request, Long userId) {
		User user = loadUserPort.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		JobPosting jobPosting = loadJobPostingPort.findById(request.jobPostingId())
			.orElseThrow(() -> new BusinessException(ErrorCode.JOB_POSTING_NOT_FOUND));

		// 중복 북마크 체크
		if (loadBookmarkPort.existsByUserAndJobPosting(user, jobPosting)) {
			throw new BusinessException(ErrorCode.BOOKMARK_ALREADY_EXISTS);
		}

		Bookmark bookmark = Bookmark.builder().user(user).jobPosting(jobPosting).build();

		return saveBookmarkPort.saveBookmark(bookmark).getId();
	}

}
