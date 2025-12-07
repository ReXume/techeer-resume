package com.techeer.backend.api.application.adapter.out.persistence;

import com.techeer.backend.api.application.domain.Application;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationJpaRepository extends JpaRepository<Application, Long> {
	boolean existsByUserAndJobPosting(User user, JobPosting jobPosting);
}
