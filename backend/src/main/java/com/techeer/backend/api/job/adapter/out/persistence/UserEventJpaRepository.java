package com.techeer.backend.api.job.adapter.out.persistence;

import com.techeer.backend.api.job.domain.EventType;
import com.techeer.backend.api.job.domain.UserEvent;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEventJpaRepository extends JpaRepository<UserEvent, Long> {

    Page<UserEvent> findByUserIdAndEventType(Long userId, EventType eventType, Pageable pageable);

    /**
     * 이벤트 타입별 job_posting_id 상위 N개 조회 (이벤트 발생 횟수 기준 내림차순)
     */
    @Query("SELECT e FROM UserEvent e WHERE e.eventType = :eventType AND e.jobPostingId IS NOT NULL "
        + "GROUP BY e.jobPostingId ORDER BY COUNT(e.jobPostingId) DESC")
    List<UserEvent> findTopJobPostingsByEventType(@Param("eventType") EventType eventType, Pageable pageable);

}
