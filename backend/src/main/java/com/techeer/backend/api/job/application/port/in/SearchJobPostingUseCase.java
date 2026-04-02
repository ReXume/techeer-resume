package com.techeer.backend.api.job.application.port.in;

import com.techeer.backend.api.job.dto.request.JobSearchRequest;
import com.techeer.backend.api.job.dto.response.AutocompleteResponse;
import com.techeer.backend.api.job.dto.response.JobSearchResponse;

public interface SearchJobPostingUseCase {

	JobSearchResponse search(JobSearchRequest request);

	AutocompleteResponse autocomplete(String prefix);

	JobSearchResponse findSimilar(Long jobPostingId, int page, int size);

}
