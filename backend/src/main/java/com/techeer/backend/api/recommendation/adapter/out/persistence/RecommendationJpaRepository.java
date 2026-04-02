package com.techeer.backend.api.recommendation.adapter.out.persistence;

import com.techeer.backend.api.recommendation.domain.Recommendation;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationJpaRepository extends JpaRepository<Recommendation, Long> {

	@Query("SELECT r FROM Recommendation r WHERE r.userId = :userId AND r.deletedAt IS NULL ORDER BY r.matchScore.value DESC")
	Page<Recommendation> findAllByUserIdAndNotDeleted(@Param("userId") Long userId, Pageable pageable);

	@Query("SELECT r FROM Recommendation r WHERE r.userId = :userId AND r.deletedAt IS NULL ORDER BY r.matchScore.value DESC")
	List<Recommendation> findTopByUserId(@Param("userId") Long userId, Pageable pageable);

	@Modifying
	@Query("UPDATE Recommendation r SET r.deletedAt = CURRENT_TIMESTAMP WHERE r.userId = :userId AND r.deletedAt IS NULL")
	void softDeleteAllByUserId(@Param("userId") Long userId);

}
