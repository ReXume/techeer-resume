package com.techeer.backend.api.resume.repository;

import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>, ResumeRepositoryQueryDsl {

    @Query("SELECT r FROM Resume r WHERE r.user.username = :username")
    List<Resume> findResumesByUsername(@Param("username") String username);

    Optional<Resume> findByIdAndDeletedAtIsNull(Long resumeId);

    Resume findFirstByUserOrderByCreatedAtDesc(User user);

    Slice<Resume> findResumeByUser(User user);
}
