package com.techeer.backend.api.document.adapter.out.persistence;

import com.techeer.backend.api.document.application.port.out.SavePortfolioPort;
import com.techeer.backend.api.document.domain.Portfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PortfolioPersistenceAdapter implements SavePortfolioPort {

    private final PortfolioJpaRepository portfolioJpaRepository;

    @Override
    public Portfolio savePortfolio(Portfolio portfolio) {
        return portfolioJpaRepository.save(portfolio);
    }
}

