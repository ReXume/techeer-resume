package com.techeer.backend.api.document.adapter.out.persistence;

import com.techeer.backend.api.document.application.port.out.LoadEducationPort;
import com.techeer.backend.api.document.application.port.out.SaveEducationPort;
import com.techeer.backend.api.document.domain.Education;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EducationPersistenceAdapter implements SaveEducationPort, LoadEducationPort {

    private final EducationJpaRepository educationJpaRepository;

    @Override
    public Education saveEducation(Education education) {
        return educationJpaRepository.save(education);
    }

    @Override
    public Optional<Education> findById(Long id) {
        // Soft Delete 적용: 삭제되지 않은 학력만 조회
        return educationJpaRepository.findByIdAndNotDeleted(id);
    }

    @Override
    public Slice<Education> findAllByUser(User user, Pageable pageable) {
        // Soft Delete 적용: 삭제되지 않은 학력만 조회
        return educationJpaRepository.findAllByUserAndNotDeleted(user, pageable);
    }

}
