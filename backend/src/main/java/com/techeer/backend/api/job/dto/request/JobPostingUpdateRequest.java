package com.techeer.backend.api.job.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JobPostingUpdateRequest(
    @NotNull(message = "사용자 ID는 필수입니다")
    Long userId,

    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다")
    String title,

    String contents,

    Integer expYears
) {}

