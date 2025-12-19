package com.techeer.backend.api.document.adapter.out.persistence;

import com.techeer.backend.api.document.application.port.out.LoadPortfolioPort;
import com.techeer.backend.api.document.application.port.out.SavePortfolioPort;
import com.techeer.backend.api.document.domain.Portfolio;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PortfolioPersistenceAdapter implements SavePortfolioPort, LoadPortfolioPort {

    private final PortfolioJpaRepository portfolioJpaRepository;

    @Override
    public Portfolio savePortfolio(Portfolio portfolio) {
        return portfolioJpaRepository.save(portfolio);
    }

    @Override
    public Optional<Portfolio> findById(Long id) {
        return portfolioJpaRepository.findByIdAndNotDeleted(id);
    }

    @Override
    public Slice<Portfolio> findAllByUser(User user, Pageable pageable) {
        return portfolioJpaRepository.findAllByUserAndNotDeleted(user, pageable);
    }
}

