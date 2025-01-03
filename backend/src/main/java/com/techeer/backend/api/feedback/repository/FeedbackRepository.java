package com.techeer.backend.api.feedback.repository;

import com.techeer.backend.api.feedback.domain.Feedback;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByIdAndResumeId(Long id, Long resumeId);

    List<Feedback> findAllByResumeId(Long resumeId);

    Optional<Feedback> findByResumeId(Long resumeId);
}
