package com.techeer.backend.api.company.application.port.out;

import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.company.domain.CompanyMember;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;

public interface LoadCompanyMemberPort {

    Optional<CompanyMember> findByUserAndCompany(User user, Company company);

}
