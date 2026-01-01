package com.techeer.backend.api.company.application.port.in;

import com.techeer.backend.api.company.dto.request.CompanyUpdateRequest;

public interface UpdateCompanyUseCase {

    void updateCompany(Long companyId, CompanyUpdateRequest request, Long userId);

}
