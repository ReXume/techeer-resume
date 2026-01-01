package com.techeer.backend.api.company.application.port.out;

import com.techeer.backend.api.company.domain.CompanyMember;

public interface SaveCompanyMemberPort {

    CompanyMember saveCompanyMember(CompanyMember companyMember);

}
