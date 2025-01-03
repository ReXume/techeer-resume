package com.techeer.backend.api.aifeedback.repository;

import com.techeer.backend.api.aifeedback.domain.AIFeedback;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AIFeedbackRepository extends JpaRepository<AIFeedback, Long> {
    Optional<AIFeedback> findByResumeId(Long resumeId);
}
