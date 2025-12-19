package com.techeer.backend.api.document.adapter.out.persistence;

import com.techeer.backend.api.document.domain.Education;
import com.techeer.backend.api.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EducationJpaRepository extends JpaRepository<Education, Long> {
    
    /**
     * 삭제되지 않은 학력 조회
     * Soft Delete 적용: deletedAt IS NULL인 경우만 조회
     */
    @Query("SELECT e FROM Education e WHERE e.id = :id AND e.deletedAt IS NULL")
    Optional<Education> findByIdAndNotDeleted(@Param("id") Long id);
    
    /**
     * 특정 사용자의 삭제되지 않은 학력 전체 조회 (Slice 페이지네이션)
     * UserFile을 통해 User를 찾아서 조회
     */
    @Query("SELECT e FROM Education e " +
           "WHERE e.file.user = :user AND e.deletedAt IS NULL " +
           "ORDER BY e.createdAt DESC")
    Slice<Education> findAllByUserAndNotDeleted(@Param("user") User user, Pageable pageable);
}

