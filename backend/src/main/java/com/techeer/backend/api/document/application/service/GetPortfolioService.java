package com.techeer.backend.api.document.application.service;

import com.techeer.backend.api.document.application.port.in.GetPortfolioUseCase;
import com.techeer.backend.api.document.application.port.out.LoadPortfolioPort;
import com.techeer.backend.api.document.domain.Portfolio;
import com.techeer.backend.api.document.dto.response.PortfolioInfoResponse;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPortfolioService implements GetPortfolioUseCase {

	private final LoadPortfolioPort loadPortfolioPort;

	@Override
	public PortfolioInfoResponse getPortfolio(Long portfolioId) {
		Portfolio portfolio = loadPortfolioPort.findById(portfolioId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PORTFOLIO_NOT_FOUND));

		return PortfolioInfoResponse.builder()
			.id(portfolio.getId())
			.title(portfolio.getTitle())
			.fileUrl(portfolio.getFile().getFileUrl())
			.isDefault(portfolio.getIsDefault())
			.build();
	}

}
