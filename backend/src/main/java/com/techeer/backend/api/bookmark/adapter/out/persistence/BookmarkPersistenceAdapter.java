package com.techeer.backend.api.bookmark.adapter.out.persistence;

import com.techeer.backend.api.bookmark.application.port.out.LoadBookmarkPort;
import com.techeer.backend.api.bookmark.application.port.out.SaveBookmarkPort;
import com.techeer.backend.api.bookmark.domain.Bookmark;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
        return bookmarkJpaRepository.findById(id);
    }
}
