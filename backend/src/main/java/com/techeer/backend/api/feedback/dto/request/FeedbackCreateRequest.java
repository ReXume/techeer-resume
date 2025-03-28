package com.techeer.backend.api.feedback.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class FeedbackCreateRequest {

    @NotBlank(message = "content는 필수입니다.")
    private String content;

    // 두 꼭짓점 좌표
    @NotNull(message = "x1 좌표는 필수입니다.")
    @JsonProperty("x1")
    private Double x1;

    @NotNull(message = "y1 좌표는 필수입니다.")
    @JsonProperty("y1")
    private Double y1;

    @NotNull(message = "x2 좌표는 필수입니다.")
    @JsonProperty("x2")
    private Double x2;

    @NotNull(message = "y2 좌표는 필수입니다.")
    @JsonProperty("y2")
    private Double y2;

    @NotNull(message = "page번호는 필수입니다.")
    private int pageNumber;
}