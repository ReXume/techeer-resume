package com.techeer.backend.api.aifeedback.controller;

import com.techeer.backend.api.aifeedback.converter.AIFeedbackConverter;
import com.techeer.backend.api.aifeedback.domain.AIFeedback;
import com.techeer.backend.api.aifeedback.dto.AIFeedbackResponse;
import com.techeer.backend.api.aifeedback.service.AIFeedbackService;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AIFeedback", description = "AI 피드백 받기")
@RestController
@RequestMapping("/api/v1/aifeedbacks")
public class AIFeedbackController {

    private final AIFeedbackService aifeedbackService;

    public AIFeedbackController(AIFeedbackService aifeedbackService) {
        this.aifeedbackService = aifeedbackService;
    }

    @Operation(summary = "AI 피드백 생성", description = "본인 이력서에 대한 AI 피드백을 진행합니다.")
    @PostMapping("/{resume_id}")
    public ResponseEntity<ApiResponse<AIFeedbackResponse>> createFeedbackFromS3(@PathVariable("resume_id") Long resumeId) {
        AIFeedback feedback = aifeedbackService.generateAIFeedbackFromS3(resumeId);
        AIFeedbackResponse feedbackResponse = AIFeedbackConverter.toResponse(feedback);
        return ResponseEntity.ok(ApiResponse.created(feedbackResponse, SuccessCode.CREATED.getMessage()));
    }

    @Operation(summary = "단일 피드백 조회", description = "피드백 ID를 통해 단일 AI 피드백을 조회합니다.")
    @GetMapping("/{aifeedback_id}")
    public ResponseEntity<ApiResponse<AIFeedbackResponse>> getFeedbackById(@PathVariable("aifeedback_id") Long aifeedbackId) {
        AIFeedback aifeedback = aifeedbackService.getFeedbackById(aifeedbackId);
        AIFeedbackResponse response = AIFeedbackConverter.toResponse(aifeedback);
        return ResponseEntity.ok(ApiResponse.success(response, SuccessCode.OK.getMessage()));
    }

}