package com.techeer.backend.api.document.adapter.in.web;

import com.techeer.backend.api.document.application.port.in.CreatePortfolioUseCase;
import com.techeer.backend.api.document.dto.request.PortfolioCreateRequest;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Portfolio", description = "포트폴리오 API")
@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final CreatePortfolioUseCase createPortfolioUseCase;
    private final UserService userService;

    @Operation(summary = "포트폴리오 등록", description = "새로운 포트폴리오를 등록합니다. 파일과 함께 업로드하세요.")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<Long>> createPortfolio(
        @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
        @Valid @RequestPart("request") PortfolioCreateRequest request,
        @RequestPart("file") MultipartFile file
    ) {
        Long userId = userService.getLoginUser().getId();
        Long portfolioId = createPortfolioUseCase.createPortfolio(request, file, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(SuccessCode.PORTFOLIO_CREATE_SUCCESS, portfolioId));
    }
}
