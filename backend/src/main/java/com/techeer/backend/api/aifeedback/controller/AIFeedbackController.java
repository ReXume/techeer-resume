package com.techeer.backend.api.aifeedback.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techeer.backend.api.aifeedback.dto.AIFeedbackResponse;
import com.techeer.backend.api.aifeedback.service.AIFeedbackService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AIFeedback", description = "AIFeedback 받기")
@RestController
@RequestMapping("/api/v1/aifeedbacks")
public class AIFeedbackController {
	private final AIFeedbackService aifeedbackService;

	public AIFeedbackController(AIFeedbackService aifeedbackService) {
		this.aifeedbackService = aifeedbackService;
	}

	@Operation(summary = "AIFeedback 생성", description = "본인 이력서에 대한 ai 피드백을 진행합니다.")
	@PostMapping("/{resume_id}")
	public ResponseEntity<AIFeedbackResponse> createFeedbackFromS3(@PathVariable("resume_id") Long resumeId) {
		AIFeedbackResponse feedbackResponse = aifeedbackService.generateAIFeedbackFromS3(resumeId);
		return ResponseEntity.status(HttpStatus.CREATED).body(feedbackResponse);
	}
}
