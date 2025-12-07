package com.techeer.backend.api.document.adapter.out.persistence;

import com.techeer.backend.api.document.application.port.out.SaveEducationPort;
import com.techeer.backend.api.document.domain.Education;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EducationPersistenceAdapter implements SaveEducationPort {

    private final EducationJpaRepository educationJpaRepository;

    @Override
    public Education saveEducation(Education education) {
        return educationJpaRepository.save(education);
    }
}

