package com.techeer.backend.api.feedback.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// @ToString
public class FeedbackCreateRequest {

    @NotBlank(message = "content는 필수입니다.")
    private String content;

    @NotNull(message = "x 좌표는 필수입니다.")
    @JsonProperty("x_coordinate")
    private Double xCoordinate;

    @NotNull(message = "y 좌표는 필수입니다.")
    @JsonProperty("y_coordinate")
    private Double yCoordinate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double xcoordinate = null;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Double ycoordinate = null;

    @NotNull(message = "page번호는 필수입니다")
    private int pageNumber;
}
