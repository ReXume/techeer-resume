package com.techeer.backend.api.job.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record AutocompleteResponse(
	List<String> suggestions
) {

}
