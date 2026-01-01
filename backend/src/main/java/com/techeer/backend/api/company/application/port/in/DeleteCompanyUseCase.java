package com.techeer.backend.api.company.application.port.in;

public interface DeleteCompanyUseCase {

    void deleteCompany(Long companyId, Long userId);

}
