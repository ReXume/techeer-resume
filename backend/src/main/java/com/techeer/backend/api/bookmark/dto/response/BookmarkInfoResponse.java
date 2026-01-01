package com.techeer.backend.api.bookmark.dto.response;

import lombok.Builder;

@Builder
public record BookmarkInfoResponse(

		Long id,


		Long jobPostingId,


		String companyName,


		String jobTitle

) {

}
