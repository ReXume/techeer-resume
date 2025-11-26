package com.techeer.backend.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.success.SuccessCode;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private boolean success;

	private String message;

	private T data;

	private String errorCode;

	private LocalDateTime timestamp;

	public static <T> ApiResponse<T> success(SuccessCode successCode) {
		return ApiResponse.<T>builder()
			.success(true)
			.message(successCode.getMessage())
			.timestamp(LocalDateTime.now())
			.build();
	}

	public static <T> ApiResponse<T> success(SuccessCode successCode, T data) {
		return ApiResponse.<T>builder()
			.success(true)
			.message(successCode.getMessage())
			.data(data)
			.timestamp(LocalDateTime.now())
			.build();
	}

	public static <T> ApiResponse<T> error(ErrorCode errorCode) {
		return ApiResponse.<T>builder()
			.success(false)
			.message(errorCode.getMessage())
			.errorCode(errorCode.getCode())
			.timestamp(LocalDateTime.now())
			.build();
	}

	public static <T> ApiResponse<T> error(ErrorCode errorCode, T data) {
		return ApiResponse.<T>builder()
			.success(false)
			.message(errorCode.getMessage())
			.errorCode(errorCode.getCode())
			.data(data)
			.timestamp(LocalDateTime.now())
			.build();
	}

}
