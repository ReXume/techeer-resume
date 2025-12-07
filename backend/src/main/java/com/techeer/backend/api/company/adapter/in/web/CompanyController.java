package com.techeer.backend.api.company.adapter.in.web;

import com.techeer.backend.api.company.application.port.in.DeleteCompanyUseCase;
import com.techeer.backend.api.company.application.port.in.GetCompanyUseCase;
import com.techeer.backend.api.company.application.port.in.RegisterCompanyUseCase;
import com.techeer.backend.api.company.application.port.in.UpdateCompanyUseCase;
import com.techeer.backend.api.company.dto.request.CompanyRegisterRequest;
import com.techeer.backend.api.company.dto.request.CompanyUpdateRequest;
import com.techeer.backend.api.company.dto.response.CompanyInfoResponse;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Company", description = "기업 API")
@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final RegisterCompanyUseCase registerCompanyUseCase;
    private final GetCompanyUseCase getCompanyUseCase;
    private final UpdateCompanyUseCase updateCompanyUseCase;
    private final DeleteCompanyUseCase deleteCompanyUseCase;

    @Operation(summary = "기업 등록", description = "새로운 기업 정보를 등록합니다. 등록한 사용자는 자동으로 관리자가 됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> registerCompany(@Valid @RequestBody CompanyRegisterRequest request) {
        Long companyId = registerCompanyUseCase.registerCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(SuccessCode.COMPANY_REGISTER_SUCCESS, companyId));
    }

    @Operation(summary = "기업 단건 조회", description = "기업 ID로 기업 정보를 조회합니다.")
    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyInfoResponse>> getCompany(@PathVariable Long companyId) {
        CompanyInfoResponse response = getCompanyUseCase.getCompany(companyId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @Operation(summary = "기업 정보 수정", description = "기업 정보를 수정합니다. 기업 관리자 권한이 필요합니다.")
    @PutMapping("/{companyId}")
    public ResponseEntity<ApiResponse<Void>> updateCompany(
        @PathVariable Long companyId,
        @Valid @RequestBody CompanyUpdateRequest request
    ) {
        updateCompanyUseCase.updateCompany(companyId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.COMPANY_UPDATE_SUCCESS));
    }

    @Operation(summary = "기업 삭제", description = "기업을 삭제합니다. 기업 관리자 권한이 필요합니다.")
    @DeleteMapping("/{companyId}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(
        @PathVariable Long companyId,
        @RequestParam Long userId
    ) {
        deleteCompanyUseCase.deleteCompany(companyId, userId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.COMPANY_DELETE_SUCCESS));
    }
}
