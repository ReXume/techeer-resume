package com.techeer.backend.api.company.adapter.out.persistence;

import com.techeer.backend.api.company.domain.Company;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyJpaRepository extends JpaRepository<Company, Long> {
	Optional<Company> findByName(String name);
}

