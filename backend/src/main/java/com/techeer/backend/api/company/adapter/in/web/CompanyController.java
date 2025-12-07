package com.techeer.backend.api.company.adapter.in.web;

import com.techeer.backend.api.company.application.port.in.RegisterCompanyUseCase;
import com.techeer.backend.api.company.dto.request.CompanyRegisterRequest;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Company", description = "기업 API")
@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

	private final RegisterCompanyUseCase registerCompanyUseCase;

	@Operation(summary = "기업 등록", description = "새로운 기업 정보를 등록합니다. 등록한 사용자는 자동으로 관리자가 됩니다.")
	@PostMapping
	public ResponseEntity<ApiResponse<Long>> registerCompany(@Valid @RequestBody CompanyRegisterRequest request) {
		Long companyId = registerCompanyUseCase.registerCompany(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success(SuccessCode.COMPANY_REGISTER_SUCCESS, companyId));
	}
}
