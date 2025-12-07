package com.techeer.backend.api.user.adapter.in.web;

import static org.springframework.http.HttpStatus.CREATED;

import com.techeer.backend.api.user.converter.UserConverter;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.dto.request.LoginRequest;
import com.techeer.backend.api.user.dto.request.RegisterRequest;
import com.techeer.backend.api.user.dto.request.SignUpRequest;
import com.techeer.backend.api.user.dto.response.UserInfoResponse;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "회원 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

	private final UserService userService;

	@Operation(summary = "자체 회원가입", description = "이메일/비밀번호로 회원가입합니다.")
	@PostMapping("/auth/register")
	public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid RegisterRequest request) {
		userService.register(request);
		return ResponseEntity.status(CREATED).body(ApiResponse.success(SuccessCode.USER_REGISTER_SUCCESS));
	}

	@Operation(summary = "자체 로그인", description = "이메일/비밀번호로 로그인합니다.")
	@PostMapping("/auth/login")
	public ResponseEntity<ApiResponse<Void>> login(@RequestBody @Valid LoginRequest request,
			HttpServletResponse response) {
		userService.login(request, response);

		return ResponseEntity.ok(ApiResponse.success(SuccessCode.USER_LOGIN_SUCCESS));
	}

	@Operation(summary = "유저 정보")
	@GetMapping("/user")
	public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo() {

		User user = userService.getLoginUser();
		UserInfoResponse result = UserConverter.INSTANCE.toUserInfoResponse(user);

		return ResponseEntity.ok(ApiResponse.success(SuccessCode.USER_FETCH_OK, result));
	}

	@Operation(summary = "추가정보 입력")
	@PostMapping("/user")
	public ResponseEntity<ApiResponse<Void>> signupUser(@RequestBody @Valid SignUpRequest req) {
		userService.signup(req);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.USER_ADDITIONAL_INFO_OK));
	}

	@Operation(summary = "로그아웃")
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logoutUser() {
		userService.logout();
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.USER_LOGOUT_OK));
	}

	@Operation(summary = "액세스 토큰 재발급")
	@PostMapping("/reissue")
	public ResponseEntity<ApiResponse<Void>> reGenerateAccessToken(
			@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {

		userService.reissueAccessToken(refreshToken, response);

		return ResponseEntity.ok(ApiResponse.success(SuccessCode.TOKEN_REISSUE_OK));
	}

	@Operation(summary = "모의 유저 데이터 생성")
	@PostMapping("/mock/signup")
	public ResponseEntity<ApiResponse<String>> mockSignup(@RequestParam(name = "id") String id) {
		String accessToken = userService.mockSignup(id);
		return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, accessToken));
	}

	@Operation(summary = "프로필 이미지 수정", description = "현재 로그인한 사용자의 프로필 이미지를 업로드하고 업데이트합니다.")
	@PatchMapping("/user/profile-image")
	public ResponseEntity<ApiResponse<String>> updateProfileImage(@RequestParam("file") MultipartFile file) {
		String profileImageUrl = userService.updateProfileImage(file);

		return ResponseEntity.ok(ApiResponse.success(SuccessCode.USER_PROFILE_IMAGE_UPDATE_OK, profileImageUrl));
	}

}

