package com.techeer.backend.api.bookmark.application.port.out;

import com.techeer.backend.api.bookmark.domain.Bookmark;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;

public interface LoadBookmarkPort {
    boolean existsByUserAndJobPosting(User user, JobPosting jobPosting);
    Optional<Bookmark> findById(Long id);
}
