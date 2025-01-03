package com.techeer.backend.global.oauth.handle;

import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.global.jwt.service.JwtService;
import com.techeer.backend.global.oauth.oauth2user.CustomOAuth2User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        log.info("OAuth2 Login 성공!");

        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
            String refreshToken = jwtService.createRefreshToken();

            // 이메일로 사용자가 이미 있는지 확인
            userRepository.findByUsernameAndSocialType(oAuth2User.getName(), oAuth2User.getSocialType())
                    .ifPresent(user -> {
                        // 기존 유저는 RefreshToken 업데이트
                        user.updateRefreshToken(refreshToken);
                        userRepository.saveAndFlush(user);

                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");

                        // 쿠키에 accessToken과 refreshToken 저장
                        addTokenCookies(response, accessToken, refreshToken);

                        // 로그인 성공 처리
                        String redirectUrl = "http://localhost:5173";
                        try {
                            response.sendRedirect(redirectUrl);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

        } catch (Exception e) {
            throw e;
        }

    }

    private void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        // Access Token 쿠키 생성
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true); // 클라이언트에서 자바스크립트를 통해 접근하지 못하도록 설정
        // accessTokenCookie.setSecure(true); // HTTPS에서만 전송되도록 설정 (개발 환경에서는 필요에 따라 설정)
        accessTokenCookie.setPath("/"); // 쿠키가 모든 경로에 적용되도록 설정
        accessTokenCookie.setMaxAge(60 * 60); // 쿠키의 만료 시간 설정 (예: 1시간)

        // Refresh Token 쿠키 생성
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        // refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 예: 7일

        // 응답에 쿠키 추가
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

}