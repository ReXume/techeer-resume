package com.techeer.backend.api.document.adapter.out.persistence;

import com.techeer.backend.api.document.domain.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EducationJpaRepository extends JpaRepository<Education, Long> {
}

