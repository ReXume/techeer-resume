package com.techeer.backend.api.document.adapter.out.persistence;

import com.techeer.backend.api.document.domain.Resume;
import com.techeer.backend.api.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeJpaRepository extends JpaRepository<Resume, Long> {

	/**
	 * 삭제되지 않은 이력서 조회 Soft Delete 적용: deletedAt IS NULL인 경우만 조회
	 */
	@Query("SELECT r FROM Resume r WHERE r.id = :id AND r.deletedAt IS NULL")
	Optional<Resume> findByIdAndNotDeleted(@Param("id") Long id);

	/**
	 * 사용자의 모든 이력서 조회 (Slice 페이지네이션) Soft Delete 적용: deletedAt IS NULL인 경우만 조회 최신순 정렬
	 */
	@Query("SELECT r FROM Resume r WHERE r.file.user = :user AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
	Slice<Resume> findAllByUserAndNotDeleted(@Param("user") User user, Pageable pageable);

}
