package com.techeer.backend.api.company.adapter.out.persistence;

import com.techeer.backend.api.company.application.port.out.SaveCompanyMemberPort;
import com.techeer.backend.api.company.domain.CompanyMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CompanyMemberPersistenceAdapter implements SaveCompanyMemberPort {

	private final CompanyMemberJpaRepository companyMemberJpaRepository;

	@Override
	public CompanyMember saveCompanyMember(CompanyMember companyMember) {
		return companyMemberJpaRepository.save(companyMember);
	}
}

