package com.techeer.backend.api.tag.company.service;

import com.techeer.backend.api.tag.company.domain.Company;
import com.techeer.backend.api.tag.company.repository.CompanyRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {
    private final CompanyRepository companyRepository;

    public List<Company> findOrCreateCompanies(List<String> companyNames) {
        return companyNames.stream()
                .map(companyName -> findByName(companyName)
                        .orElseGet(() -> save(new Company(companyName))))
                .collect(Collectors.toList());
    }

    public Optional<Company> findByName(String name) {
        // 이름으로 Company를 찾는 로직 구현
        return companyRepository.findByName(name);
    }

    public Company save(Company company) {
        // Company 저장 로직 구현
        return companyRepository.save(company);
    }

    public Company saveCompany(String companyName) {
        return companyRepository.save(
                Company.builder()
                        .name(companyName)
                        .build()
        );
    }

    public List<Company> getCompaniesByName(List<String> companyNames) {
        return companyRepository.findByNameIn(companyNames);
    }
}
