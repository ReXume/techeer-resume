package com.techeer.backend.api.job.adapter.out.persistence;

import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.job.domain.JobPostingStatus;
import com.techeer.backend.api.job.domain.SourceType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostingJpaRepository extends JpaRepository<JobPosting, Long> {

	/**
	 * 삭제되지 않은 채용공고 조회 Soft Delete 적용: deletedAt IS NULL인 경우만 조회
	 */
	@Query("SELECT j FROM JobPosting j WHERE j.id = :id AND j.deletedAt IS NULL")
	Optional<JobPosting> findByIdAndNotDeleted(@Param("id") Long id);

	/**
	 * 크롤러 중복 감지: externalId + source 조합으로 조회
	 */
	@Query("SELECT j FROM JobPosting j WHERE j.sourceInfo.externalId = :externalId AND j.sourceInfo.source = :source AND j.deletedAt IS NULL")
	Optional<JobPosting> findByExternalIdAndSource(
		@Param("externalId") String externalId,
		@Param("source") SourceType source
	);

	/**
	 * 기간이 있는 OPEN 상태 채용공고 조회 (만료 처리용)
	 */
	@Query("SELECT j FROM JobPosting j WHERE j.sourceInfo.source = :source AND j.status = :status AND j.deadlineType = com.techeer.backend.api.job.domain.DeadlineType.FIXED AND j.deletedAt IS NULL")
	List<JobPosting> findOpenPostingsWithDeadlineBySource(
		@Param("source") SourceType source,
		@Param("status") JobPostingStatus status
	);

	/**
	 * 증분 동기화: 마지막 동기화 이후 수정된 채용공고 조회
	 */
	@Query("SELECT j FROM JobPosting j WHERE j.updatedAt > :since")
	List<JobPosting> findByUpdatedAtAfter(@Param("since") LocalDateTime since);

}
