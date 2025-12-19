package com.techeer.backend.api.file.adapter.out.persistence;

import com.techeer.backend.api.file.domain.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFileJpaRepository extends JpaRepository<UserFile, Long> {

}
