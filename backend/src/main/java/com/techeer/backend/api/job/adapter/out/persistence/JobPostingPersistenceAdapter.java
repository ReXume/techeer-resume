package com.techeer.backend.api.job.adapter.out.persistence;

import com.techeer.backend.api.job.application.port.out.FindJobPostingByExternalPort;
import com.techeer.backend.api.job.application.port.out.LoadJobPostingPort;
import com.techeer.backend.api.job.application.port.out.SaveJobPostingPort;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.job.domain.JobPostingStatus;
import com.techeer.backend.api.job.domain.SourceType;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JobPostingPersistenceAdapter implements SaveJobPostingPort, LoadJobPostingPort,
	FindJobPostingByExternalPort {

	private final JobPostingJpaRepository jobPostingJpaRepository;

	@Override
	public JobPosting saveJobPosting(JobPosting jobPosting) {
		return jobPostingJpaRepository.save(jobPosting);
	}

	@Override
	public Optional<JobPosting> findById(Long id) {
		// Soft Delete 적용: 삭제되지 않은 채용공고만 조회
		return jobPostingJpaRepository.findByIdAndNotDeleted(id);
	}

	@Override
	public Optional<JobPosting> findByExternalIdAndSource(String externalId, SourceType source) {
		return jobPostingJpaRepository.findByExternalIdAndSource(externalId, source);
	}

	@Override
	public List<JobPosting> findOpenPostingsWithDeadlineBySource(SourceType source) {
		return jobPostingJpaRepository.findOpenPostingsWithDeadlineBySource(source, JobPostingStatus.OPEN);
	}

}
