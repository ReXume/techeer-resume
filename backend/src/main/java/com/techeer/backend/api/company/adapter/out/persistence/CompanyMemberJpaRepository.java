package com.techeer.backend.api.company.adapter.out.persistence;

import com.techeer.backend.api.company.domain.CompanyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyMemberJpaRepository extends JpaRepository<CompanyMember, Long> {
}

