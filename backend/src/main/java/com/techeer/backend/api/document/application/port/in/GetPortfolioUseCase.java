package com.techeer.backend.api.document.application.port.in;

import com.techeer.backend.api.document.dto.response.PortfolioInfoResponse;

public interface GetPortfolioUseCase {

    PortfolioInfoResponse getPortfolio(Long portfolioId);

}
