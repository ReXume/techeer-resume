package com.techeer.backend.api.company.dto.response;

import lombok.Builder;

@Builder
public record CompanyInfoResponse(Long id, String name, String industryDomain, String websiteUrl, String location) {
}
