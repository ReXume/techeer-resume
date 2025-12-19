package com.techeer.backend.api.career.adapter.out.persistence;

import com.techeer.backend.api.career.domain.UserCareer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCareerJpaRepository extends JpaRepository<UserCareer, Long> {
    
    /**
     * 삭제되지 않은 사용자 경력 조회
     * Soft Delete 적용: deletedAt IS NULL인 경우만 조회
     */
    @Query("SELECT uc FROM UserCareer uc WHERE uc.id = :id AND uc.deletedAt IS NULL")
    Optional<UserCareer> findByIdAndNotDeleted(@Param("id") Long id);
}

