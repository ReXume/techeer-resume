package com.techeer.backend.api.bookmark.converter;

import com.techeer.backend.api.bookmark.domain.Bookmark;
import com.techeer.backend.api.bookmark.dto.BookmarkResponse;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;

public class BookmarkConverter {

    // 북마크 엔티티를 dto 변환
    public static BookmarkResponse toBookmarkResponse(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .bookmarkId(bookmark.getId())
                .resumeId(bookmark.getResume().getId())
                .resumeTitle(bookmark.getResume().getName())
                .resumeAuthor(bookmark.getResume().getUser().getUsername())
                .createdAt(bookmark.getResume().getCreatedAt())
                .build();
    }

    public static Bookmark toBookmarkEntity(User user, Resume resume) {
        return Bookmark.builder()
                .user(user)
                .resume(resume)
                .build();
    }

    // 북마크 엔티티 리스트를 dto로 변환
    public static List<BookmarkResponse> toBookmarkResponses(List<Bookmark> bookmarks) {
        return bookmarks.stream()
                .map(BookmarkConverter::toBookmarkResponse)
                .collect(Collectors.toList());
    }
}
