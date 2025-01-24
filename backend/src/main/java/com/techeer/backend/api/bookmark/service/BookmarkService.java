package com.techeer.backend.api.bookmark.service;

import com.techeer.backend.api.bookmark.converter.BookmarkConverter;
import com.techeer.backend.api.bookmark.domain.Bookmark;
import com.techeer.backend.api.bookmark.repository.BookmarkRepository;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.repository.ResumeRepository;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ResumeRepository resumeRepository;

    @Transactional
    public Bookmark addBookmark(User user, Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));
        
        Bookmark bookmark = BookmarkConverter.toBookmarkEntity(user, resume);

        return bookmarkRepository.save(bookmark);
    }


    @Transactional
    public void removeBookmark(User user, Long bookmarkId) {

        // bookmark_id로 북마크 조회
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKMARK_NOT_FOUND));

        if (!bookmark.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 북마크 삭제
        bookmarkRepository.delete(bookmark);
    }

    // user_id로 모든 북마크 조회
    public List<Bookmark> getBookmarksByUserId(Long userId) {
        return bookmarkRepository.findAllByUserId(userId);
    }
}
