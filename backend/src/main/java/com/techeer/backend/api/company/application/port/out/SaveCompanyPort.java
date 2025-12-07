package com.techeer.backend.api.company.application.port.out;

import com.techeer.backend.api.company.domain.Company;

public interface SaveCompanyPort {
	Company saveCompany(Company company);
}

