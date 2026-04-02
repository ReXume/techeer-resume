package com.techeer.backend.api.job.application.port.out;

import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.job.dto.request.SearchCriteria;
import com.techeer.backend.api.job.dto.response.AutocompleteResponse;
import com.techeer.backend.api.job.dto.response.JobSearchResponse;

public interface SearchJobPostingPort {

	void indexJobPosting(JobPosting jobPosting);

	JobSearchResponse searchJobPostings(SearchCriteria criteria);

	AutocompleteResponse autocomplete(String prefix);

	JobSearchResponse findSimilar(Long jobPostingId, int page, int size);

	void deleteFromIndex(Long id);

}
