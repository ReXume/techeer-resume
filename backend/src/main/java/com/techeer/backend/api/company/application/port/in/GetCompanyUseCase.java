package com.techeer.backend.api.company.application.port.in;

import com.techeer.backend.api.company.dto.response.CompanyInfoResponse;

public interface GetCompanyUseCase {

    CompanyInfoResponse getCompany(Long companyId);

}
