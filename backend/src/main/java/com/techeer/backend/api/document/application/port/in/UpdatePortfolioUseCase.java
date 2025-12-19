package com.techeer.backend.api.document.application.port.in;

import com.techeer.backend.api.document.dto.request.PortfolioUpdateRequest;

public interface UpdatePortfolioUseCase {

	void updatePortfolio(Long portfolioId, PortfolioUpdateRequest request, Long userId);

}
