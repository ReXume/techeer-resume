package com.techeer.backend.api.feedback.service;

import com.techeer.backend.api.aifeedback.domain.AIFeedback;
import com.techeer.backend.api.aifeedback.repository.AIFeedbackRepository;
import com.techeer.backend.api.feedback.converter.FeedbackConverter;
import com.techeer.backend.api.feedback.domain.Feedback;
import com.techeer.backend.api.feedback.dto.request.FeedbackCreateRequest;
import com.techeer.backend.api.feedback.repository.FeedbackRepository;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.exception.ResumeNotFoundException;
import com.techeer.backend.api.resume.repository.ResumeRepository;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final ResumeRepository resumeRepository;
    private final AIFeedbackRepository aiFeedbackRepository;

    @Transactional
    public Feedback createFeedback(User user, Long resumeId, FeedbackCreateRequest feedbackCreateRequest) {

        Resume resume = resumeRepository.findByIdAndDeletedAtIsNull(resumeId)
                .orElseThrow(ResumeNotFoundException::new);

        Feedback feedback = FeedbackConverter.toFeedbackEntity(user, resume, feedbackCreateRequest);
        feedbackRepository.save(feedback);

        return feedback;
    }

    @Transactional
    public void deleteFeedbackById(User user, Long resumeId, Long feedbackId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FEEDBACK_NOT_FOUND));

        if (!feedback.getResume().getId().equals(resume.getId())) {
            throw new BusinessException(ErrorCode.INVALID_FEEDBACK_FOR_RESUME);
        }

        if (!feedback.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        log.info("피드백 삭제 중: 피드백 ID {} (이력서 ID {})", feedbackId, resumeId);

        feedbackRepository.delete(feedback);
    }

    public List<Feedback> getFeedbackByResumeId(Long resumeId) {
        // 이력서 존재 확인
        if (!resumeRepository.existsById(resumeId)) {
            throw new BusinessException(ErrorCode.RESUME_NOT_FOUND);
        }
        // 이력서 id에 해당하는 모든 일반 피드백 가져옴
        List<Feedback> feedbacks = feedbackRepository.findAllByResumeId(resumeId);

        return feedbacks;
    }

    public AIFeedback getAIFeedbackByResumeId(Long resumeId) {
        // 이력서 id에 해당하는 ai 피드백 가져옴(없으면 빈배열 반환)
        return aiFeedbackRepository.findByResumeId(resumeId)
                .orElse(AIFeedback.empty());
    }

    public List<Feedback> getFeedbacksByResumeId(Long resumeId) {
        return feedbackRepository.findAllByResumeId(resumeId);
    }
}
