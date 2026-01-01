package com.techeer.backend.api.skill.adapter.out.persistence;

import com.techeer.backend.api.skill.domain.Skill;
import com.techeer.backend.api.skill.domain.UserSkill;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSkillJpaRepository extends JpaRepository<UserSkill, Long> {

	boolean existsByUserAndSkill(User user, Skill skill);

	/**
	 * 삭제되지 않은 사용자 스킬 조회 Soft Delete 적용: deletedAt IS NULL인 경우만 조회
	 */
	@Query("SELECT us FROM UserSkill us WHERE us.id = :id AND us.deletedAt IS NULL")
	Optional<UserSkill> findByIdAndNotDeleted(@Param("id") Long id);

}
