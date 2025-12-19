package com.techeer.backend.api.document.dto.response;

import lombok.Builder;

@Builder
public record PortfolioInfoResponse(Long id, String title, String fileUrl, Boolean isDefault) {
}
