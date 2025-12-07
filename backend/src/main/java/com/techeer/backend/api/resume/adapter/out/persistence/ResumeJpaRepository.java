package com.techeer.backend.api.resume.adapter.out.persistence;

import com.techeer.backend.api.resume.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeJpaRepository extends JpaRepository<Resume, Long> {
}

