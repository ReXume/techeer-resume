package com.techeer.backend.api.resume.repository;

import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.dto.request.ResumeSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepositoryQueryDsl {
    Page<Resume> searchByCriteria(ResumeSearchRequest req, Pageable pageable);
}