package com.techeer.backend.api.company.application.service;

import com.techeer.backend.api.company.application.port.in.UpdateCompanyUseCase;
import com.techeer.backend.api.company.application.port.out.LoadCompanyMemberPort;
import com.techeer.backend.api.company.application.port.out.LoadCompanyPort;
import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.company.domain.CompanyMember;
import com.techeer.backend.api.company.domain.CompanyRole;
import com.techeer.backend.api.company.dto.request.CompanyUpdateRequest;
import com.techeer.backend.api.user.application.port.out.LoadUserPort;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateCompanyService implements UpdateCompanyUseCase {

    private final LoadCompanyPort loadCompanyPort;
    private final LoadUserPort loadUserPort;
    private final LoadCompanyMemberPort loadCompanyMemberPort;

    @Override
    public void updateCompany(Long companyId, CompanyUpdateRequest request, Long userId) {
        Company company = loadCompanyPort.findById(companyId)
            .orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

        User user = loadUserPort.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        CompanyMember member = loadCompanyMemberPort.findByUserAndCompany(user, company)
            .orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_MEMBER_NOT_FOUND));

        if (member.getRole() != CompanyRole.ADMIN) {
            throw new BusinessException(ErrorCode.COMPANY_FORBIDDEN);
        }

        company.updateInfo(
            request.name(),
            request.industryDomain(),
            request.websiteUrl(),
            request.location()
        );
    }
}

