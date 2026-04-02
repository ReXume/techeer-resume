package com.techeer.backend.api.job.application.service;

import com.techeer.backend.api.job.application.port.in.SearchJobPostingUseCase;
import com.techeer.backend.api.job.application.port.out.SearchJobPostingPort;
import com.techeer.backend.api.job.dto.request.JobSearchRequest;
import com.techeer.backend.api.job.dto.request.SearchCriteria;
import com.techeer.backend.api.job.dto.response.AutocompleteResponse;
import com.techeer.backend.api.job.dto.response.JobSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchJobPostingService implements SearchJobPostingUseCase {

	private final SearchJobPostingPort searchJobPostingPort;

	@Override
	public JobSearchResponse search(JobSearchRequest request) {
		SearchCriteria criteria = SearchCriteria.from(request);
		return searchJobPostingPort.searchJobPostings(criteria);
	}

	@Override
	public AutocompleteResponse autocomplete(String prefix) {
		return searchJobPostingPort.autocomplete(prefix);
	}

	@Override
	public JobSearchResponse findSimilar(Long jobPostingId, int page, int size) {
		return searchJobPostingPort.findSimilar(jobPostingId, page, size);
	}

}
