package com.techeer.backend.api.aifeedback.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "AIFEEDBACK")
public class AIFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long resumeId;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String feedback;

    // 기본 생성자
    protected AIFeedback() {
    }

    // 생성자
    @Builder
    public AIFeedback(Long resumeId, String feedback) {
        this.resumeId = resumeId;
        this.feedback = feedback;
    }

    public static AIFeedback empty() {
        return AIFeedback.builder()
                .resumeId(-1L) // 존재하지 않는 이력서의 ID를 의미하는 값으로 음수를 사용
                .feedback("No AI Feedback available") // 기본 메시지 설정
                .build();
    }
}
