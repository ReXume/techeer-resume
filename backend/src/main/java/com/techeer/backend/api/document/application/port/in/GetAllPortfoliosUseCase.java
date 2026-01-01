package com.techeer.backend.api.document.application.port.in;

import com.techeer.backend.api.document.dto.response.PortfolioInfoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GetAllPortfoliosUseCase {

    Slice<PortfolioInfoResponse> getAllPortfolios(Long userId, Pageable pageable);

}
