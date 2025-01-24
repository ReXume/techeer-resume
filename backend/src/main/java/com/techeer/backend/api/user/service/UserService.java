package com.techeer.backend.api.user.service;


import com.techeer.backend.api.user.domain.Role;
import com.techeer.backend.api.user.domain.SocialType;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.dto.request.SignUpRequest;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import com.techeer.backend.global.jwt.JwtToken;
import com.techeer.backend.global.jwt.service.JwtService;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    public void signup(SignUpRequest signUpReq) {
        User user = this.getLoginUser();
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            user.updateUser(signUpReq);
            userRepository.save(user);
        }
    }

    public void CreateRegularUser(Map<String, Object> attributes, String name, SocialType socialType) {
        User user = User.builder()
                .email((String) attributes.get("email"))
                .username(name)
                .socialType(socialType)
                .role(Role.REGULAR)
                .build();
        userRepository.save(user);
    }

    public void logout() {
        User user = this.getLoginUser();
        user.onLogout();
        userRepository.save(user);
    }

    public User getLoginUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("userDetails = " + userDetails);
        // 유저 정보 조회
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public JwtToken reissueAccessToken(String refreshToken) {

        // Refresh Token 검증
        if (!jwtService.isRefreshTokenValid(refreshToken)) {return null;}

        User user = this.getLoginUser();
        return JwtToken.builder()
                .accessToken(jwtService.createAccessToken(user.getEmail()))
                .refreshToken(jwtService.reIssueRefreshToken(user))
                .build();
    }

    public String mockSignup(String id) {
        String accessToken = jwtService.createAccessToken(id);
        String refreshToken = jwtService.createRefreshToken();

        User user = User.builder()
                .refreshToken(refreshToken)
                .email(id)
                .username("mock")
                .role(Role.REGULAR)
                .socialType(SocialType.GOOGLE)
                .build();
        userRepository.save(user);

        return accessToken;
    }
}
