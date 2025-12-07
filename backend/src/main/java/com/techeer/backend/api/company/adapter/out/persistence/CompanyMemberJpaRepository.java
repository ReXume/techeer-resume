package com.techeer.backend.api.company.adapter.out.persistence;

import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.company.domain.CompanyMember;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyMemberJpaRepository extends JpaRepository<CompanyMember, Long> {
    Optional<CompanyMember> findByUserAndCompany(User user, Company company);
}
