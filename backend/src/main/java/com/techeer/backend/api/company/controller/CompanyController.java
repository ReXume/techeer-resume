package com.techeer.backend.api.company.controller;

import com.techeer.backend.api.company.dto.request.CompanyRegisterRequest;
import com.techeer.backend.api.company.service.CompanyService;
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
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

	private final CompanyService companyService;

	@PostMapping
	public ResponseEntity<ApiResponse<Long>> registerCompany(@Valid @RequestBody CompanyRegisterRequest request) {
		Long companyId = companyService.registerCompany(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.COMPANY_REGISTER_SUCCESS, companyId));
	}
}
