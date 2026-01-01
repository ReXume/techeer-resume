package com.techeer.backend.api.company.adapter.out.persistence;

import com.techeer.backend.api.company.application.port.out.LoadCompanyMemberPort;
import com.techeer.backend.api.company.application.port.out.SaveCompanyMemberPort;
import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.company.domain.CompanyMember;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CompanyMemberPersistenceAdapter implements SaveCompanyMemberPort, LoadCompanyMemberPort {

    private final CompanyMemberJpaRepository companyMemberJpaRepository;

    @Override
    public CompanyMember saveCompanyMember(CompanyMember companyMember) {
        return companyMemberJpaRepository.save(companyMember);
    }

    @Override
    public Optional<CompanyMember> findByUserAndCompany(User user, Company company) {
        return companyMemberJpaRepository.findByUserAndCompany(user, company);
    }

}
