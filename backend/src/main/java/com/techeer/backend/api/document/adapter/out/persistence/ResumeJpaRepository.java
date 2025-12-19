package com.techeer.backend.api.document.adapter.out.persistence;

import com.techeer.backend.api.document.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeJpaRepository extends JpaRepository<Resume, Long> {
    
    /**
     * 삭제되지 않은 이력서 조회
     * Soft Delete 적용: deletedAt IS NULL인 경우만 조회
     */
    @Query("SELECT r FROM Resume r WHERE r.id = :id AND r.deletedAt IS NULL")
    Optional<Resume> findByIdAndNotDeleted(@Param("id") Long id);
}

