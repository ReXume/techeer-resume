package com.techeer.backend.api.bookmark.repository;

import com.techeer.backend.api.bookmark.domain.Bookmark;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 특정 사용자의 모든 북마크를 조회
    List<Bookmark> findAllByUserId(Long userId);
}

