package com.techeer.backend.api.document.adapter.in.web;

import com.techeer.backend.api.document.application.port.in.CreatePortfolioUseCase;
import com.techeer.backend.api.document.application.port.in.DeletePortfolioUseCase;
import com.techeer.backend.api.document.application.port.in.GetAllPortfoliosUseCase;
import com.techeer.backend.api.document.application.port.in.GetPortfolioUseCase;
import com.techeer.backend.api.document.application.port.in.UpdatePortfolioUseCase;
import com.techeer.backend.api.document.dto.request.PortfolioCreateRequest;
import com.techeer.backend.api.document.dto.request.PortfolioUpdateRequest;
import com.techeer.backend.api.document.dto.response.PortfolioInfoResponse;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final UpdatePortfolioUseCase updatePortfolioUseCase;
    private final DeletePortfolioUseCase deletePortfolioUseCase;
    private final GetPortfolioUseCase getPortfolioUseCase;
    private final GetAllPortfoliosUseCase getAllPortfoliosUseCase;
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

    @Operation(summary = "포트폴리오 단건 조회", description = "포트폴리오 ID로 특정 포트폴리오를 조회합니다.")
    @GetMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<PortfolioInfoResponse>> getPortfolio(
        @PathVariable Long portfolioId
    ) {
        PortfolioInfoResponse response = getPortfolioUseCase.getPortfolio(portfolioId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, response));
    }

    @Operation(summary = "포트폴리오 전체 조회", description = "현재 로그인한 사용자의 포트폴리오 목록을 조회합니다. (Slice 페이지네이션)")
    @GetMapping
    public ResponseEntity<ApiResponse<Slice<PortfolioInfoResponse>>> getAllPortfolios(
        @PageableDefault(size = 10) Pageable pageable
    ) {
        Long userId = userService.getLoginUser().getId();
        Slice<PortfolioInfoResponse> response = getAllPortfoliosUseCase.getAllPortfolios(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GET_SUCCESS, response));
    }

    @Operation(summary = "포트폴리오 수정", description = "포트폴리오 정보를 수정합니다.")
    @PutMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<Void>> updatePortfolio(
        @PathVariable Long portfolioId,
        @Valid @RequestBody PortfolioUpdateRequest request
    ) {
        Long userId = userService.getLoginUser().getId();
        updatePortfolioUseCase.updatePortfolio(portfolioId, request, userId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.UPDATE_SUCCESS));
    }

    @Operation(summary = "포트폴리오 삭제", description = "포트폴리오를 삭제합니다. (Soft Delete)")
    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(
        @PathVariable Long portfolioId
    ) {
        Long userId = userService.getLoginUser().getId();
        deletePortfolioUseCase.deletePortfolio(portfolioId, userId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.DELETE_SUCCESS));
    }
}
