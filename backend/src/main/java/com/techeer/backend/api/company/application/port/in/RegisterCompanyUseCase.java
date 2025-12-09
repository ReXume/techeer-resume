package com.techeer.backend.api.company.application.port.in;

import com.techeer.backend.api.company.dto.request.CompanyRegisterRequest;

public interface RegisterCompanyUseCase {
	Long registerCompany(CompanyRegisterRequest request, Long userId);
}

