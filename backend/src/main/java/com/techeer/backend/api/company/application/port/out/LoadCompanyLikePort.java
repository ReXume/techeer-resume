package com.techeer.backend.api.company.application.port.out;

import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.user.domain.User;

public interface LoadCompanyLikePort {
	boolean existsByUserAndCompany(User user, Company company);
}

