package com.techeer.backend.api.company.application.port.in;

public interface UnlikeCompanyUseCase {

    void unlikeCompany(Long companyLikeId, Long userId);

}
