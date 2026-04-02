package com.techeer.backend.api.user.dto.request;

import jakarta.validation.constraints.Size;

public record UserProfileUpdateRequest(
	@Size(max = 100) String desiredPosition,
	@Size(max = 50) String experienceLevel,
	@Size(max = 500) String preferredLocations,
	@Size(max = 100) String preferredCompanySize,
	Boolean openToRemote
) {

}
