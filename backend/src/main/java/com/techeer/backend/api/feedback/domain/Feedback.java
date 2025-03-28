package com.techeer.backend.api.feedback.domain;

import com.techeer.backend.api.feedback.dto.request.FeedbackCreateRequest;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "FEEDBACK")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 한 개의 이력서가 여러 개의 피드백을 가질 수 있음
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false, length = 255)
    private String content;

    // 두 꼭짓점을 이용하여 영역을 표현
    @Column(nullable = false)
    private Double x1;

    @Column(nullable = false)
    private Double y1;

    @Column(nullable = false)
    private Double x2;

    @Column(nullable = false)
    private Double y2;

    @Column(nullable = false)
    private int pageNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 빌더 메서드 예시
    public static Feedback of(User user, Resume resume, FeedbackCreateRequest request) {
        if (request.getPageNumber() <= 0) {
            throw new IllegalArgumentException("페이지번호는 1 이상의 양수여야 합니다.");
        }
        return Feedback.builder()
                .user(user)
                .resume(resume)
                .content(request.getContent())
                .x1(request.getX1())
                .y1(request.getY1())
                .x2(request.getX2())
                .y2(request.getY2())
                .pageNumber(request.getPageNumber())
                .build();
    }
}
