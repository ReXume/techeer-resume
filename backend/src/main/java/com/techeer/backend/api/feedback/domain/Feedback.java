package com.techeer.backend.api.feedback.domain;

import com.techeer.backend.api.feedback.dto.request.FeedbackCreateRequest;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "FEEDBACK")
@Builder
@AllArgsConstructor
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 한 개의 이력서가 여러 개의 피드백을 가질 수 있음.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    private Double xCoordinate;

    @Column(nullable = false)
    private Double yCoordinate;

    @Column(nullable = false)
    private int pageNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 기본 생성자
    protected Feedback() {
    }

    // Builder 패턴을 사용한 생성자
    @Builder
    public static Feedback of(User user, Resume resume, FeedbackCreateRequest feedbackCreateRequest) {
        if (feedbackCreateRequest.getPageNumber() <= 0) {
            throw new IllegalArgumentException("페이지번호는 양수입니다.");
        }
        return Feedback.builder()
                .user(user)
                .resume(resume)
                .content(feedbackCreateRequest.getContent())
                .xCoordinate(feedbackCreateRequest.getXCoordinate())
                .yCoordinate(feedbackCreateRequest.getYCoordinate())
                .pageNumber(feedbackCreateRequest.getPageNumber())
                .build();
    }
}
