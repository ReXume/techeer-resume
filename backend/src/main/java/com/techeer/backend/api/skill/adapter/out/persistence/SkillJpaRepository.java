package com.techeer.backend.api.skill.adapter.out.persistence;

import com.techeer.backend.api.skill.domain.Skill;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillJpaRepository extends JpaRepository<Skill, Long> {

	Optional<Skill> findByName(String name);

	Optional<Skill> findByNameIgnoreCase(String name);

	/**
	 * 삭제되지 않은 기술 스택 조회 Soft Delete 적용: deletedAt IS NULL인 경우만 조회
	 */
	@Query("SELECT s FROM Skill s WHERE s.id = :id AND s.deletedAt IS NULL")
	Optional<Skill> findByIdAndNotDeleted(@Param("id") Long id);

	/**
	 * 삭제되지 않은 기술 스택을 이름으로 조회 (대소문자 구분 없이) Soft Delete 적용: deletedAt IS NULL인 경우만 조회
	 */
	@Query("SELECT s FROM Skill s WHERE LOWER(s.name) = LOWER(:name) AND s.deletedAt IS NULL")
	Optional<Skill> findByNameIgnoreCaseAndNotDeleted(@Param("name") String name);

}
