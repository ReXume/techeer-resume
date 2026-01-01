package com.techeer.backend.api.document.adapter.out.persistence;

import com.techeer.backend.api.document.domain.Portfolio;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioJpaRepository extends JpaRepository<Portfolio, Long> {

    @Query("SELECT p FROM Portfolio p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Portfolio> findByIdAndNotDeleted(@Param("id") Long id);

    @Query("SELECT p FROM Portfolio p WHERE p.file.user = :user AND p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    Slice<Portfolio> findAllByUserAndNotDeleted(@Param("user") User user, Pageable pageable);

}
