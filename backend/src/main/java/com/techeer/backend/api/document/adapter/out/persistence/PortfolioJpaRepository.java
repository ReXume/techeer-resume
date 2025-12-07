package com.techeer.backend.api.document.adapter.out.persistence;

import com.techeer.backend.api.document.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioJpaRepository extends JpaRepository<Portfolio, Long> {
}

