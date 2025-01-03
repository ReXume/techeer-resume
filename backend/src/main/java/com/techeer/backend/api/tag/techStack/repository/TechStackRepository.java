package com.techeer.backend.api.tag.techStack.repository;

import com.techeer.backend.api.tag.techStack.domain.TechStack;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechStackRepository extends JpaRepository<TechStack, Long> {
    List<TechStack> findByNameIn(List<String> techStackNames);

    Optional<TechStack> findByName(String name);
}
