package com.techeer.backend.api.company.application.port.out;

import com.techeer.backend.api.company.domain.Company;
import java.util.Optional;

public interface LoadCompanyPort {
	Optional<Company> findByName(String name);

	Optional<Company> findById(Long id);
}
