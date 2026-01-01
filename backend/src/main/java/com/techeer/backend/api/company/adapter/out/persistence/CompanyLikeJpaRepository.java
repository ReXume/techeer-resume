package com.techeer.backend.api.company.adapter.out.persistence;

import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.company.domain.CompanyLike;
import com.techeer.backend.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyLikeJpaRepository extends JpaRepository<CompanyLike, Long> {

    boolean existsByUserAndCompany(User user, Company company);

}
