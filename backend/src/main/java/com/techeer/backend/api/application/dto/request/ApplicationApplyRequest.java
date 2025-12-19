package com.techeer.backend.api.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record ApplicationApplyRequest(@NotNull(message = "채용공고 ID는 필수입니다") Long jobPostingId) {
}
