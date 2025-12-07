package com.techeer.backend.api.career.adapter.in.web;

import com.techeer.backend.api.career.application.port.in.CreateUserCareerUseCase;
import com.techeer.backend.api.career.dto.request.UserCareerCreateRequest;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/careers")
@RequiredArgsConstructor
public class UserCareerController {

	private final CreateUserCareerUseCase createUserCareerUseCase;

	@PostMapping
	public ResponseEntity<ApiResponse<Long>> createUserCareer(@Valid @RequestBody UserCareerCreateRequest request) {
		Long careerId = createUserCareerUseCase.createUserCareer(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.USER_CAREER_CREATE_SUCCESS, careerId));
	}
}

