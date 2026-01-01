package com.techeer.backend.api.bookmark.adapter.out.persistence;

import com.techeer.backend.api.bookmark.application.port.out.LoadBookmarkPort;
import com.techeer.backend.api.bookmark.application.port.out.SaveBookmarkPort;
import com.techeer.backend.api.bookmark.domain.Bookmark;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookmarkPersistenceAdapter implements SaveBookmarkPort, LoadBookmarkPort {

	private final BookmarkJpaRepository bookmarkJpaRepository;

	@Override
	public Bookmark saveBookmark(Bookmark bookmark) {
		return bookmarkJpaRepository.save(bookmark);
	}

	@Override
	public boolean existsByUserAndJobPosting(User user, JobPosting jobPosting) {
		return bookmarkJpaRepository.existsByUserAndJobPosting(user, jobPosting);
	}

	@Override
	public Optional<Bookmark> findById(Long id) {
		// Soft Delete 적용: 삭제되지 않은 북마크만 조회
		return bookmarkJpaRepository.findByIdAndNotDeleted(id);
	}

	@Override
	public Slice<Bookmark> findAllByUser(User user, Pageable pageable) {
		return bookmarkJpaRepository.findAllByUserAndNotDeleted(user, pageable);
	}

}
