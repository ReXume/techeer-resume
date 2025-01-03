package com.techeer.backend.api.resume.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageableResumeResponse {
    private final Long resumeId;
    private final String userName;
    private final String resumeName;
    private final String position;
    private final Integer career;
    private final List<String> techStackNames;
    private List<String> companyNames;
    private int totalPage;
    private int currentPage;
}
