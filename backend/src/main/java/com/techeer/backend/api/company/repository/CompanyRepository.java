package com.techeer.backend.api.company.repository;

import com.techeer.backend.api.company.domain.Company;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
	Optional<Company> findByName(String name);
}

