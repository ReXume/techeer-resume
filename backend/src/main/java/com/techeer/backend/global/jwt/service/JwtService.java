package com.techeer.backend.global.jwt.service;

import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.repository.UserRepository;
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
import java.util.Date;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
			.setSubject(ACCESS_TOKEN_SUBJECT) // JWT의 Subject 지정 -> AccessToken이므로
												// AccessToken
			.setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod)) // 토큰 만료
																					// 시간
																					// 설정
			.claim(EMAIL_CLAIM, email)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public String reIssueRefreshToken(User user) {
		String reIssuedRefreshToken = this.createRefreshToken();
		user.updateRefreshToken(reIssuedRefreshToken);
		userRepository.saveAndFlush(user);
		return reIssuedRefreshToken;
	}

	public String createRefreshToken() {
		Date now = new Date();
		String newRefreshToken = Jwts.builder()
			.setSubject(REFRESH_TOKEN_SUBJECT)
			.setExpiration(new Date(now.getTime() + refreshTokenExpirationPeriod))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();

		return newRefreshToken;
	}

	/**
	 * HttpServletRequest에서 Access Token을 추출합니다.
	 * 우선순위: 1. Cookie (기본 인증 방식) 2. Authorization 헤더 (Swagger UI 테스트용)
	 */
	public Optional<String> extractAccessToken(HttpServletRequest request) {
		// 1. 쿠키에서 먼저 확인 (프로덕션 환경의 기본 인증 방식)
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("accessToken".equals(cookie.getName())) {
					log.info("Access Token이 쿠키에서 추출되었습니다.");
					return Optional.of(cookie.getValue());
				}
			}
		}

		// 2. Authorization 헤더에서 확인 (Swagger UI 등 테스트 도구용)
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith(BEARER)) {
			String token = authHeader.substring(BEARER.length());
			log.info("Access Token이 Authorization 헤더에서 추출되었습니다.");
			return Optional.of(token);
		}

		log.debug("Access Token을 찾을 수 없습니다.");
		return Optional.empty();
	}

	public boolean isAccessTokenValid(String token) {
		try {
			Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		}
		catch (Exception e) {
			log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
			return false;
		}
	}

	public boolean isRefreshTokenValid(String refreshToken) {
		// 만료시간 검증
		if (isTokenExpired(refreshToken)) {
			return false;
		}

		// DB에 refreshToken이 유효성 검증
		Optional<User> user = userRepository.findByRefreshToken(refreshToken);
		if (user.isEmpty()) {
			return false;
		}

		return user.get().getRefreshToken() != null && user.get().getRefreshToken().equals(refreshToken);
	}

	public boolean isTokenExpired(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
			Date expiration = claims.getExpiration();
			return expiration.before(new Date());
		}
		catch (JwtException e) {
			log.error("Refresh Token이 만료되었습니다. {}", e.getMessage());
			return true;
		}
	}

	/**
	 * Access Token에서 사용자 이메일을 추출합니다.
	 * @param accessToken JWT Access Token
	 * @return 이메일 (추출 실패 시 null)
	 */
	public String extractEmail(String accessToken) {
		try {
			Claims claims = decodeToken(accessToken);
			return claims != null ? claims.get(EMAIL_CLAIM, String.class) : null;
		} catch (Exception e) {
			log.error("토큰에서 이메일 추출 실패: {}", e.getMessage());
			return null;
		}
	}

	private Claims decodeToken(String token) {
		return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
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
