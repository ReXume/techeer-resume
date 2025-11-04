package com.techeer.backend.api.user.service;

import com.techeer.backend.api.user.domain.Role;
import com.techeer.backend.api.user.domain.SocialType;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.dto.request.LoginRequest;
import com.techeer.backend.api.user.dto.request.RegisterRequest;
import com.techeer.backend.api.user.dto.request.SignUpRequest;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import com.techeer.backend.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 추가 정보 입력
     * 비즈니스 로직: 현재 로그인한 사용자의 추가 정보 업데이트
     */
    @Transactional
    public void signup(SignUpRequest signUpReq) {
        User user = this.getLoginUser();
        user.updateUser(signUpReq);
        // @Transactional 내에서 엔티티 수정 시 변경 감지로 자동 저장되므로 save() 불필요
    }

    /**
     * 자체 회원가입 (이메일/비밀번호)
     * 비즈니스 로직: 이메일 중복 확인, 비밀번호 암호화, 사용자 생성
     * 보안: 자체 회원가입 시 항상 REGULAR 역할로 고정하여 권한 상승 방지
     */
    @Transactional
    public void register(RegisterRequest request) {
        // 이메일 중복 확인
        validateEmailNotExists(request.getEmail());

        // 비밀번호 암호화 후 사용자 생성
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(encodedPassword)
                .role(Role.REGULAR) // 자체 회원가입 시 항상 REGULAR 역할 부여
                .socialType(null)
                .build();

        // 새로운 엔티티이므로 save() 필요
        userRepository.save(user);
        log.info("새로운 사용자 가입 완료: email={}, role=REGULAR", request.getEmail());
    }

    /**
     * 자체 로그인 (이메일/비밀번호)
     * 비즈니스 로직: 사용자 인증, 토큰 생성, 쿠키 설정
     */
    @Transactional
    public void login(LoginRequest request, HttpServletResponse response) {
        // 사용자 조회
        User user = findUserByEmail(request.getEmail());

        // 소셜 로그인 사용자는 자체 로그인 불가
        if (user.getSocialType() != null) {
            throw new BusinessException(ErrorCode.USER_SOCIAL_LOGIN_ONLY);
        }

        // 비밀번호 확인
        validatePassword(request.getPassword(), user.getPassword());

        // 토큰 생성 및 저장
        String accessToken = jwtService.createAccessToken(user.getEmail());
        String refreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(refreshToken);
        // @Transactional 내에서 엔티티 수정 시 변경 감지로 자동 저장

        // 쿠키에 토큰 저장
        jwtService.addTokenCookies(response, accessToken, refreshToken);
        log.info("사용자 로그인 완료: email={}", request.getEmail());
    }

    /**
     * 소셜 로그인 사용자 생성 (OAuth2용)
     * 비즈니스 로직: 소셜 로그인 정보를 기반으로 사용자 생성
     * 
     * Note: CustomOAuth2UserService에서 이미 중복 체크를 수행하므로
     * 이 메서드 호출 시점에는 신규 사용자임이 보장됨
     */
    @Transactional
    public void createRegularUser(Map<String, Object> attributes, String name, SocialType socialType) {
        String email = (String) attributes.get("email");
        
        // 새로운 사용자 생성
        User user = User.builder()
                .email(email)
                .username(name)
                .socialType(socialType)
                .role(Role.REGULAR)
                .build();
        
        userRepository.save(user);
        log.info("새로운 소셜 로그인 사용자 생성 완료: email={}, socialType={}", email, socialType);
    }

    /**
     * 로그아웃
     * 비즈니스 로직: 현재 사용자의 RefreshToken 제거
     */
    @Transactional
    public void logout() {
        User user = this.getLoginUser();
        user.onLogout();
        // @Transactional 내에서 엔티티 수정 시 변경 감지로 자동 저장
        log.info("사용자 로그아웃 완료: email={}", user.getEmail());
    }

    /**
     * 현재 로그인한 사용자 조회
     * SecurityContext에서 인증 정보를 가져와 사용자 엔티티 반환
     */
    public User getLoginUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        
        String email = userDetails.getUsername();
        log.debug("현재 로그인한 사용자 조회: email={}", email);
        
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 액세스 토큰 재발급
     * 비즈니스 로직: RefreshToken 검증, AccessToken 재발급, RefreshToken 갱신, 쿠키 설정
     * 
     * 주의: AccessToken이 만료된 상태에서 호출되므로 SecurityContext에서 사용자를
     * 조회할 수 없습니다. 따라서 RefreshToken으로 DB에서 사용자를 조회합니다.
     */
    @Transactional
    public void reissueAccessToken(String refreshToken, HttpServletResponse response) {
        // RefreshToken 검증
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // RefreshToken으로 사용자 조회 (AccessToken이 만료되어 SecurityContext에 없을 수 있음)
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));
        
        // DB의 RefreshToken과 일치하는지 한 번 더 확인
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        
        // 새로운 AccessToken 생성
        String accessToken = jwtService.createAccessToken(user.getEmail());
        
        // RefreshToken 갱신 (보안 강화)
        String newRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(newRefreshToken);
        // @Transactional 내에서 엔티티 수정 시 변경 감지로 자동 저장

        // 쿠키에 토큰 저장
        jwtService.addTokenCookies(response, accessToken, newRefreshToken);
        log.info("토큰 재발급 완료: email={}", user.getEmail());
    }

    /**
     * 모의 사용자 생성 (테스트용)
     * 비즈니스 로직: 테스트를 위한 모의 사용자 생성 및 토큰 반환
     */
    @Transactional
    public String mockSignup(String id) {
        // 이미 존재하는 사용자 확인
        userRepository.findByEmail(id)
                .ifPresent(user -> {
                    log.warn("모의 사용자가 이미 존재합니다: email={}", id);
                    throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
                });

        String accessToken = jwtService.createAccessToken(id);
        String refreshToken = jwtService.createRefreshToken();

        User user = User.builder()
                .email(id)
                .username("mock")
                .password(null)
                .refreshToken(refreshToken)
                .role(Role.REGULAR)
                .socialType(SocialType.GOOGLE)
                .build();
        
        userRepository.save(user);
        log.info("모의 사용자 생성 완료: email={}", id);

        return accessToken;
    }

    // ========== Private Helper Methods ==========

    /**
     * 이메일로 사용자 조회
     */
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 이메일 중복 확인
     */
    private void validateEmailNotExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }

    /**
     * 비밀번호 일치 확인
     */
    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_MISMATCH);
        }
    }
}
