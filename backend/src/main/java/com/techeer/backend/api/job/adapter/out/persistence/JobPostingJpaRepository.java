package com.techeer.backend.api.job.adapter.out.persistence;

import com.techeer.backend.api.job.domain.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostingJpaRepository extends JpaRepository<JobPosting, Long> {
}

