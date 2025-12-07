package com.techeer.backend.api.career.adapter.out.persistence;

import com.techeer.backend.api.career.domain.UserCareer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCareerJpaRepository extends JpaRepository<UserCareer, Long> {
}

