package com.techeer.backend.api.resume.dto.response;

import lombok.Builder;

@Builder
public record ResumeInfoResponse(
    Long id,
    String title,
    String fileUrl,
    Boolean isDefault
) {}

