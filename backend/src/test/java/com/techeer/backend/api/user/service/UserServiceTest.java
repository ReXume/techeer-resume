package com.techeer.backend.api.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.techeer.backend.api.user.domain.Role;
import com.techeer.backend.api.user.domain.SocialType;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.dto.request.LoginRequest;
import com.techeer.backend.api.user.dto.request.RegisterRequest;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.global.error.exception.BusinessException;
import com.techeer.backend.global.jwt.service.JwtService;
import com.techeer.backend.infra.gcp.FileTypeMapper;
import com.techeer.backend.infra.gcp.GcsUploader;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtService jwtService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private GcsUploader gcsUploader;

	@Mock
	private FileTypeMapper fileTypeMapper;

	@Nested
	@DisplayName("자체 회원가입 (Register)")
	class Register {

		@Test
		@DisplayName("정상적인 회원가입 요청 시 사용자가 저장되어야 한다")
		void success() {
			// Given
			RegisterRequest request = new RegisterRequest("test@example.com", "Test User", "password1234");
			given(userRepository.findByEmail(request.email())).willReturn(Optional.empty());
			given(passwordEncoder.encode(request.password())).willReturn("encodedPassword");

			// When
			userService.register(request);

			// Then
			verify(userRepository).save(any(User.class));
		}

		@Test
		@DisplayName("이미 존재하는 이메일로 가입 시 예외가 발생해야 한다")
		void fail_duplicate_email() {
			// Given
			RegisterRequest request = new RegisterRequest("test@example.com", "Test User", "password1234");
			given(userRepository.findByEmail(request.email())).willReturn(Optional.of(mock(User.class)));

			// When & Then
			assertThatThrownBy(() -> userService.register(request)).isInstanceOf(BusinessException.class)
				.hasMessageContaining("이미 존재하는 이메일입니다");
		}

	}

	@Nested
	@DisplayName("자체 로그인 (Login)")
	class Login {

		@Test
		@DisplayName("이메일과 비밀번호가 일치하면 토큰이 발급되어야 한다")
		void success() {
			// Given
			LoginRequest request = new LoginRequest("test@example.com", "password");
			User user = User.builder()
				.email("test@example.com")
				.password("encodedPassword")
				.role(Role.USER)
				.build();
			HttpServletResponse response = mock(HttpServletResponse.class);

			given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
			given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(true);
			given(jwtService.createAccessToken(any())).willReturn("accessToken");
			given(jwtService.createRefreshToken()).willReturn("refreshToken");

			// When
			userService.login(request, response);

			// Then
			verify(jwtService).addTokenCookies(response, "accessToken", "refreshToken");
			assertThat(user.getRefreshToken()).isEqualTo("refreshToken");
		}

		@Test
		@DisplayName("존재하지 않는 이메일로 로그인 시 예외가 발생해야 한다")
		void fail_user_not_found() {
			// Given
			LoginRequest request = new LoginRequest("unknown@example.com", "password");
			HttpServletResponse response = mock(HttpServletResponse.class);

			given(userRepository.findByEmail(request.email())).willReturn(Optional.empty());

			// When & Then
			assertThatThrownBy(() -> userService.login(request, response)).isInstanceOf(BusinessException.class);
		}

		@Test
		@DisplayName("비밀번호가 일치하지 않으면 예외가 발생해야 한다")
		void fail_password_mismatch() {
			// Given
			LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");
			User user = User.builder()
				.email("test@example.com")
				.password("encodedPassword")
				.build();
			HttpServletResponse response = mock(HttpServletResponse.class);

			given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
			given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(false);

			// When & Then
			assertThatThrownBy(() -> userService.login(request, response)).isInstanceOf(BusinessException.class);
		}

	}

	@Nested
	@DisplayName("소셜 로그인 사용자 생성 (CreateRegularUser)")
	class CreateRegularUser {

		@Test
		@DisplayName("소셜 로그인 정보로 새로운 사용자가 저장되어야 한다")
		void success() {
			// Given
			Map<String, Object> attributes = Map.of("email", "social@example.com");
			String name = "Social User";
			SocialType socialType = SocialType.GOOGLE;

			// When
			userService.createRegularUser(attributes, name, socialType);

			// Then
			verify(userRepository).save(any(User.class));
		}

	}

}

