package com.techeer.backend.api.document.application.port.in;

import com.techeer.backend.api.document.dto.request.PortfolioCreateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface CreatePortfolioUseCase {

    Long createPortfolio(PortfolioCreateRequest request, MultipartFile file, Long userId);

}
