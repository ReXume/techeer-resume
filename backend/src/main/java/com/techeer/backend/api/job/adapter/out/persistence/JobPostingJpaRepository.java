package com.techeer.backend.api.job.adapter.out.persistence;

import com.techeer.backend.api.job.domain.JobPosting;
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

}
