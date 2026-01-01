package com.techeer.backend.api.bookmark.adapter.out.persistence;

import com.techeer.backend.api.bookmark.domain.Bookmark;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkJpaRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByUserAndJobPosting(User user, JobPosting jobPosting);

    /**
     * 삭제되지 않은 북마크 조회 Soft Delete 적용: deletedAt IS NULL인 경우만 조회
     */
    @Query("SELECT b FROM Bookmark b WHERE b.id = :id AND b.deletedAt IS NULL")
    Optional<Bookmark> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * 사용자의 모든 북마크 조회 (Slice 페이지네이션) Soft Delete 적용: deletedAt IS NULL인 경우만 조회 최신순 정렬
     */
    @Query("SELECT b FROM Bookmark b WHERE b.user = :user AND b.deletedAt IS NULL ORDER BY b.createdAt DESC")
    Slice<Bookmark> findAllByUserAndNotDeleted(@Param("user") User user, Pageable pageable);

}
