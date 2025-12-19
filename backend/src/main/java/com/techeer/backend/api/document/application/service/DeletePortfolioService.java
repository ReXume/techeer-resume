package com.techeer.backend.api.document.application.service;

import com.techeer.backend.api.document.application.port.in.DeletePortfolioUseCase;
import com.techeer.backend.api.document.application.port.out.LoadPortfolioPort;
import com.techeer.backend.api.document.domain.Portfolio;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeletePortfolioService implements DeletePortfolioUseCase {

	private final LoadPortfolioPort loadPortfolioPort;

	@Override
	public void deletePortfolio(Long portfolioId, Long userId) {
		Portfolio portfolio = loadPortfolioPort.findById(portfolioId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PORTFOLIO_NOT_FOUND));

		if (!portfolio.getFile().getUser().getId().equals(userId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}

		portfolio.softDelete();
	}

}
