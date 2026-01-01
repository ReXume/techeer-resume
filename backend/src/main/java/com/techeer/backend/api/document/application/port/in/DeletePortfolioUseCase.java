package com.techeer.backend.api.document.application.port.in;

public interface DeletePortfolioUseCase {

    void deletePortfolio(Long portfolioId, Long userId);

}
