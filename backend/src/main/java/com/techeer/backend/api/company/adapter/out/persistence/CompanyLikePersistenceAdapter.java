package com.techeer.backend.api.company.adapter.out.persistence;

import com.techeer.backend.api.company.application.port.out.LoadCompanyLikePort;
import com.techeer.backend.api.company.application.port.out.SaveCompanyLikePort;
import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.company.domain.CompanyLike;
import com.techeer.backend.api.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CompanyLikePersistenceAdapter implements SaveCompanyLikePort, LoadCompanyLikePort {

	private final CompanyLikeJpaRepository companyLikeJpaRepository;

	@Override
	public CompanyLike saveCompanyLike(CompanyLike companyLike) {
		return companyLikeJpaRepository.save(companyLike);
	}

	@Override
	public boolean existsByUserAndCompany(User user, Company company) {
		return companyLikeJpaRepository.existsByUserAndCompany(user, company);
	}

	@Override
	public Optional<CompanyLike> findById(Long id) {
		return companyLikeJpaRepository.findById(id);
	}

}
