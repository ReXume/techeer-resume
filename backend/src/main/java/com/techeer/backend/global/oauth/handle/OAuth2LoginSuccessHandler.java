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
import org.springframework.cache.annotation.CachePut;
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

            // 이메일로 사용자가 이미 있는지 확인
            userRepository.findByUsernameAndSocialType(oAuth2User.getName(), oAuth2User.getSocialType())
                    .ifPresent(user -> {
                        // AccessToken 생성
                        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());

                        // DB  RefreshToken 업데이트
                       String refreshToken = jwtService.reIssueRefreshToken(user);

                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");

                        // 쿠키에 accessToken과 refreshToken 저장
                        jwtService.addTokenCookies(response, accessToken, refreshToken);

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
}