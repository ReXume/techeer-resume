package com.techeer.backend.api.document.application.port.out;

import com.techeer.backend.api.document.domain.Portfolio;

public interface SavePortfolioPort {

    Portfolio savePortfolio(Portfolio portfolio);

}
