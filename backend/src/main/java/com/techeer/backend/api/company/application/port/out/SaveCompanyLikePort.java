package com.techeer.backend.api.company.application.port.out;

import com.techeer.backend.api.company.domain.CompanyLike;

public interface SaveCompanyLikePort {
	CompanyLike saveCompanyLike(CompanyLike companyLike);
}

