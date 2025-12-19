package com.techeer.backend.api.company.adapter.out.persistence;

import com.techeer.backend.api.company.application.port.out.LoadCompanyPort;
import com.techeer.backend.api.company.application.port.out.SaveCompanyPort;
import com.techeer.backend.api.company.domain.Company;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CompanyPersistenceAdapter implements SaveCompanyPort, LoadCompanyPort {

	private final CompanyJpaRepository companyJpaRepository;

	@Override
	public Company saveCompany(Company company) {
		return companyJpaRepository.save(company);
	}

	@Override
	public Optional<Company> findByName(String name) {
		return companyJpaRepository.findByName(name);
	}

	@Override
	public Optional<Company> findById(Long id) {
		// Soft Delete 적용: 삭제되지 않은 회사만 조회
		return companyJpaRepository.findByIdAndNotDeleted(id);
	}

}
