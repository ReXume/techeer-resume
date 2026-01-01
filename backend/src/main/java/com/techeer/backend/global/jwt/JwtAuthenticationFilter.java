package com.techeer.backend.global.jwt;

import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.global.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
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

    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 특정 경로 이외에는 필터를 건너뜀
        if (!requestURI.startsWith("/api/v1/")) {
            // log.info("requestURI: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        log.info("requestURI: {}", requestURI);
        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    /**
     * JWT Access Token을 검증하고 인증 정보를 SecurityContext에 저장합니다.
     */
    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                   FilterChain filterChain) throws ServletException, IOException {
        log.debug("JWT 인증 필터 실행: {}", request.getRequestURI());

        jwtService.extractAccessToken(request).filter(jwtService::isAccessTokenValid).flatMap(accessToken -> {
            String email = jwtService.extractEmail(accessToken);
            if (email != null) {
                log.info("유효한 Access Token 발견. 사용자: {}", email);
                return userRepository.findByEmail(email);
            }
            log.warn("토큰에서 이메일 추출 실패");
            return Optional.empty();
        }).ifPresent(user -> {
            saveAuthentication(user);
            log.info("인증 정보 저장 완료: {}", user.getEmail());
        });

        filterChain.doFilter(request, response);
    }

    /**
     * 사용자 정보를 기반으로 Spring Security 인증 객체를 생성하고 SecurityContext에 저장합니다.
     */
    private void saveAuthentication(User user) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("") // JWT 기반 인증이므로 비밀번호 불필요
                .roles(user.getRole().name())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
