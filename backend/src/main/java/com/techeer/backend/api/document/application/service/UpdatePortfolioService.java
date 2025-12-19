package com.techeer.backend.api.document.application.service;

import com.techeer.backend.api.document.application.port.in.UpdatePortfolioUseCase;
import com.techeer.backend.api.document.application.port.out.LoadPortfolioPort;
import com.techeer.backend.api.document.domain.Portfolio;
import com.techeer.backend.api.document.dto.request.PortfolioUpdateRequest;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdatePortfolioService implements UpdatePortfolioUseCase {

	private final LoadPortfolioPort loadPortfolioPort;

	@Override
	public void updatePortfolio(Long portfolioId, PortfolioUpdateRequest request, Long userId) {
		Portfolio portfolio = loadPortfolioPort.findById(portfolioId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PORTFOLIO_NOT_FOUND));

		if (!portfolio.getFile().getUser().getId().equals(userId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}

		portfolio.updateTitle(request.title());

		if (request.isDefault() != null) {
			if (request.isDefault()) {
				portfolio.setAsDefault();
			}
			else {
				portfolio.unsetAsDefault();
			}
		}
	}

}
