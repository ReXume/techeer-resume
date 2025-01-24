package com.techeer.backend.global.jwt.service;

import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.redis.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    private final UserRepository userRepository;
    private final RedisService redisService;

    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    /**
     * AccessToken 생성 메소드
     */
    public String createAccessToken(String email) {
        Date now = new Date();
        return Jwts.builder() // JWT 토큰을 생성하는 빌더 반환
                .setSubject(ACCESS_TOKEN_SUBJECT) // JWT의 Subject 지정 -> AccessToken이므로 AccessToken
                .setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod)) // 토큰 만료 시간 설정
                .claim(EMAIL_CLAIM, email)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String reIssueRefreshToken(User user) {
        String reIssuedRefreshToken = this.createRefreshToken();
        String oldRefreshToken= user.updateRefreshToken(reIssuedRefreshToken);

        if (oldRefreshToken != null) {redisService.deleteCacheRefreshToken(oldRefreshToken);}
        userRepository.saveAndFlush(user);

        redisService.cacheRefreshToken(reIssuedRefreshToken);
        return reIssuedRefreshToken;
    }


    public String createRefreshToken() {
        Date now = new Date();
        String newRefreshToken=  Jwts.builder()
                .setSubject(REFRESH_TOKEN_SUBJECT)
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return newRefreshToken;
    }



    public Optional<String> extractAccessTokenFromCookie(HttpServletRequest request) {

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    log.info("Access Token이 쿠키에서 추출되었습니다: {}", cookie.getValue());
                    return Optional.of(cookie.getValue());
                }
            }
        }

        log.warn("Access Token이 쿠키에서 발견되지 않았습니다.");
        return Optional.empty();
    }

    public boolean isAccessTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        // 만료시간 검증
        //if(isTokenExpired(refreshToken)) {return false;}

        // cache에 refreshToken이 유효성 검증
        String userRefreshToken = redisService.refreshTokenGet(refreshToken);
        if (userRefreshToken != null) {return userRefreshToken.equals(refreshToken);}

        // DB에 refreshToken이 유효성 검증
        Optional<User> user = userRepository.findByRefreshToken(refreshToken);
        userRefreshToken = user.get().getRefreshToken();
        return userRefreshToken.equals(refreshToken);
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            log.error("Refresh Token이 만료되었습니다. {}", e.getMessage());
            return true;
        }
    }

    public Object[] extractEmailAndSocialType(String accessToken) {
        Claims claims = decodeToken(accessToken);
        if (claims != null) {
            String email = claims.get("email", String.class);
            return new Object[]{email};
        }
        return null;
    }

    private Claims decodeToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
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
