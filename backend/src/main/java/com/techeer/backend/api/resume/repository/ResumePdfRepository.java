package com.techeer.backend.api.resume.repository;

import com.techeer.backend.api.resume.domain.ResumePdf;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumePdfRepository extends JpaRepository<ResumePdf, Long> {
    Optional<ResumePdf> findByResumeId(Long resumeId);
}
