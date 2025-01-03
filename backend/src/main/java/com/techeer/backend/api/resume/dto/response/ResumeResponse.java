package com.techeer.backend.api.resume.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResumeResponse {
    private final Long resumeId;
    private final String userName;
    private final Integer career;
    private final String resumeName;
    private final String position;
    private final List<String> techStackNames;
    private List<String> companyNames;
}
