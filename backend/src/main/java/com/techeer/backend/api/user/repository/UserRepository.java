package com.techeer.backend.api.user.repository;

import com.techeer.backend.api.user.domain.SocialType;
import com.techeer.backend.api.user.domain.User;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndSocialType(String username, SocialType socialType);  // 사용자 이름으로 사용자 찾기

    Optional<User> findByEmail(String email);        // 이메일로 사용자 찾기

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User>findByEmailAndSocialType(String username, SocialType socialType);
}
