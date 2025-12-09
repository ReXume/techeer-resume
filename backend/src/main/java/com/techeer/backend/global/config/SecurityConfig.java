package com.techeer.backend.global.config;

import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.global.jwt.JwtAuthenticationFilter;
import com.techeer.backend.global.jwt.service.JwtService;
import com.techeer.backend.global.oauth.handle.OAuth2LoginFailureHandler;
import com.techeer.backend.global.oauth.handle.OAuth2LoginSuccessHandler;
import com.techeer.backend.global.oauth.service.CustomOAuth2UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final UserRepository userRepository;

	private final CustomOAuth2UserService customOAuth2UserService;

	private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

	private final JwtService jwtService;

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowCredentials(true);
		// 백엔드 배포 테스트v1
		config.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:5173"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setExposedHeaders(List.of("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
			.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
			.httpBasic(HttpBasicConfigurer::disable)
			.sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// Swagger UI 및 API 문서 경로는 인증 없이 접근 가능
			// 자체 로그인 API와 소셜 로그인 경로도 permitAll
			.authorizeHttpRequests(authorize -> authorize
				// Swagger UI 및 API 문서 (가장 먼저 체크하여 OAuth2 필터에 가로채이지 않도록)
				.requestMatchers("/", "/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/index.html",
						"/swagger/**", "/v3/api-docs/**", "/api-docs/**", "/index.html",
						"/swagger-ui.html/**", "/swagger-resources/**", "/webjars/**")
				.permitAll()
				// 자체 로그인/회원가입 API
				.requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/mock/signup")
				.permitAll()
				// 소셜 로그인 관련 경로
				.requestMatchers("/oauth2/**", "/oauth2/authorization/**", "/login")
				.permitAll()
				// 기타 공개 경로
				.requestMatchers("/signup.html")
				.permitAll()
				// 나머지는 인증 필요
				.anyRequest()
				.authenticated())
			// OAuth2 소셜 로그인 설정
			.oauth2Login(oauth2Login -> oauth2Login
				.userInfoEndpoint(endpoint -> endpoint.userService(customOAuth2UserService))
				.successHandler(oAuth2LoginSuccessHandler)
				.failureHandler(oAuth2LoginFailureHandler)
			)
			// JWT 인증 필터 추가 (자체 로그인 및 소셜 로그인 후 JWT 토큰 처리)
			.addFilterBefore(new JwtAuthenticationFilter(jwtService, userRepository),
					UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

}