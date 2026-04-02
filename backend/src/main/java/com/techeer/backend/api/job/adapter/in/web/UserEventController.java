package com.techeer.backend.api.job.adapter.in.web;

import com.techeer.backend.api.job.application.service.UserEventService;
import com.techeer.backend.api.job.domain.EventType;
import com.techeer.backend.api.job.dto.request.UserEventRequest;
import com.techeer.backend.api.job.dto.response.PopularJobPostingResponse;
import com.techeer.backend.api.job.dto.response.UserEventResponse;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserEvent", description = "사용자 행동 이벤트 API")
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class UserEventController {

    private final UserEventService userEventService;

    private final UserService userService;

    @Operation(summary = "이벤트 기록", description = "사용자의 행동 이벤트를 기록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<UserEventResponse>> recordEvent(
        @Valid @RequestBody UserEventRequest request) {
        Long userId = userService.getLoginUser().getId();
        UserEventResponse response = userEventService.recordEvent(
            userId, request.jobPostingId(), request.eventType(), request.metadata());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(SuccessCode.USER_EVENT_RECORD_SUCCESS, response));
    }

    @Operation(summary = "인기 채용공고 조회", description = "조회수 기준 인기 채용공고를 반환합니다.")
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PopularJobPostingResponse>>> getPopularJobPostings(
        @RequestParam(defaultValue = "10") int limit) {
        List<PopularJobPostingResponse> response = userEventService.getPopularJobPostings(limit);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.USER_EVENT_POPULAR_SUCCESS, response));
    }

    @Operation(summary = "이벤트 히스토리 조회", description = "사용자의 이벤트 히스토리를 조회합니다.")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<UserEventResponse>>> getUserEventHistory(
        @RequestParam EventType eventType,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        Long userId = userService.getLoginUser().getId();
        Page<UserEventResponse> response = userEventService.getUserEventHistory(userId, eventType, page, size);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.USER_EVENT_HISTORY_SUCCESS, response));
    }

}
