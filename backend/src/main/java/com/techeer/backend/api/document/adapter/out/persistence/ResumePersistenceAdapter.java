package com.techeer.backend.api.document.adapter.out.persistence;

import com.techeer.backend.api.document.application.port.out.LoadResumePort;
import com.techeer.backend.api.document.application.port.out.SaveResumePort;
import com.techeer.backend.api.document.domain.Resume;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ResumePersistenceAdapter implements SaveResumePort, LoadResumePort {

    private final ResumeJpaRepository resumeJpaRepository;

    @Override
    public Resume saveResume(Resume resume) {
        return resumeJpaRepository.save(resume);
    }

    @Override
    public Optional<Resume> findById(Long id) {
        return resumeJpaRepository.findById(id);
    }
}
