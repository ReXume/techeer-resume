package com.techeer.backend.api.document.application.service;

import com.techeer.backend.api.document.application.port.in.GetAllPortfoliosUseCase;
import com.techeer.backend.api.document.application.port.out.LoadPortfolioPort;
import com.techeer.backend.api.document.dto.response.PortfolioInfoResponse;
import com.techeer.backend.api.user.application.port.out.LoadUserPort;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAllPortfoliosService implements GetAllPortfoliosUseCase {

    private final LoadPortfolioPort loadPortfolioPort;

    private final LoadUserPort loadUserPort;

    @Override
    public Slice<PortfolioInfoResponse> getAllPortfolios(Long userId, Pageable pageable) {
        User user = loadUserPort.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return loadPortfolioPort.findAllByUser(user, pageable)
                .map(portfolio -> PortfolioInfoResponse.builder()
                        .id(portfolio.getId())
                        .title(portfolio.getTitle())
                        .fileUrl(portfolio.getFile().getFileUrl())
                        .isDefault(portfolio.getIsDefault())
                        .build());
    }

}
