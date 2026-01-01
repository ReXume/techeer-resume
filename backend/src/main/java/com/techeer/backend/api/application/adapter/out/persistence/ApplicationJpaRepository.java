package com.techeer.backend.api.application.adapter.out.persistence;

import com.techeer.backend.api.application.domain.Application;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationJpaRepository extends JpaRepository<Application, Long> {

	boolean existsByUserAndJobPosting(User user, JobPosting jobPosting);

	/**
	 * 삭제되지 않은 지원서 조회 Soft Delete 적용: deletedAt IS NULL인 경우만 조회
	 */
	@Query("SELECT a FROM Application a WHERE a.id = :id AND a.deletedAt IS NULL")
	Optional<Application> findByIdAndNotDeleted(@Param("id") Long id);

}
