package com.techeer.backend.api.job.adapter.out.search;

import com.techeer.backend.api.job.adapter.out.persistence.JobPostingJpaRepository;
import com.techeer.backend.api.job.application.port.out.SearchJobPostingPort;
import com.techeer.backend.api.job.domain.JobPosting;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobPostingIndexSyncBatch {

	private final JobPostingJpaRepository jobPostingJpaRepository;

	private final SearchJobPostingPort searchJobPostingPort;

	private final AtomicReference<LocalDateTime> lastSyncTime =
		new AtomicReference<>(LocalDateTime.now().minusMinutes(5));

	/**
	 * Incremental sync: index job postings updated since last run. Runs every 5 minutes.
	 */
	@Scheduled(fixedDelay = 300_000)
	public void syncUpdatedJobPostings() {
		LocalDateTime syncFrom = lastSyncTime.get();
		LocalDateTime syncStart = LocalDateTime.now();
		log.info("Starting incremental OpenSearch sync from {}", syncFrom);

		List<JobPosting> updated = jobPostingJpaRepository.findByUpdatedAtAfter(syncFrom);
		int count = 0;
		for (JobPosting jp : updated) {
			try {
				if (jp.getDeletedAt() != null) {
					searchJobPostingPort.deleteFromIndex(jp.getId());
				}
				else {
					searchJobPostingPort.indexJobPosting(jp);
				}
				count++;
			}
			catch (Exception e) {
				log.error("Failed to sync job posting id={}: {}", jp.getId(), e.getMessage());
			}
		}

		lastSyncTime.set(syncStart);
		log.info("Incremental sync complete: {} documents processed", count);
	}

}
