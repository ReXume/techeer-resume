package com.techeer.backend.api.tag.company.repository;

import com.techeer.backend.api.tag.company.domain.Company;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByNameIn(List<String> companyNames);

    Optional<Company> findByName(String name);
}
