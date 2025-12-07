package com.techeer.backend.api.document.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResumeUpdateRequest(
    @NotNull(message = "사용자 ID는 필수입니다")
    Long userId,

    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
    String title,

    Boolean isDefault
) {}

