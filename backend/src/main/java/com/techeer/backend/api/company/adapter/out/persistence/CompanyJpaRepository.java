package com.techeer.backend.api.company.adapter.out.persistence;

import com.techeer.backend.api.company.domain.Company;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyJpaRepository extends JpaRepository<Company, Long> {
	Optional<Company> findByName(String name);
	
	/**
	 * 삭제되지 않은 회사 조회
	 * Soft Delete 적용: deletedAt IS NULL인 경우만 조회
	 */
	@Query("SELECT c FROM Company c WHERE c.id = :id AND c.deletedAt IS NULL")
	Optional<Company> findByIdAndNotDeleted(@Param("id") Long id);
}

