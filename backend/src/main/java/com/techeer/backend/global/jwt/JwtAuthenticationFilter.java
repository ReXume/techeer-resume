package com.techeer.backend.global.jwt;

import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.global.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("requestURI: {}", requestURI);
        // 특정 경로 이외에는 필터를 건너뜀
        if (!requestURI.startsWith("/api/v1/")) {
            filterChain.doFilter(request, response);
            return;
        }
        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");
        jwtService.extractAccessTokenFromCookie(request)
                .filter(jwtService::isTokenValid)
                .ifPresent(accessToken -> {
                    log.info("유효한 Access Token이 발견되었습니다: {}", accessToken);

                    // 이메일 및 소셜 타입 추출
                    Object[] emailAndSocialType = jwtService.extractEmailAndSocialType(accessToken);
                    if (emailAndSocialType.length >= 1) {
                        String email = (String) emailAndSocialType[0];
                        log.info("이메일이 추출되었습니다: {}", email);

                        // 사용자 정보 조회
                        userRepository.findByEmail(email)
                                .ifPresent(user -> {
                                    log.info("사용자 정보가 발견되었습니다: {}", user);
                                    saveAuthentication(user);
                                    log.info("사용자 인증 정보가 저장되었습니다: {}", user);
                                });
                    } else {
                        log.warn("이메일 추출 실패. 반환된 배열 길이: {}", emailAndSocialType.length);
                    }
                });
        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(User user) {

        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("Google")
                .roles(user.getRole().name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
