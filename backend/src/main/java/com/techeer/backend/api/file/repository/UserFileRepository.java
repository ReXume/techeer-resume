package com.techeer.backend.api.file.repository;

import com.techeer.backend.api.file.domain.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFileRepository extends JpaRepository<UserFile, Long> {
}

