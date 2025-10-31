package com.techeer.backend.api.user.controller;

import com.techeer.backend.api.user.converter.UserConverter;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.dto.request.SignUpRequest;
import com.techeer.backend.api.user.dto.response.UserInfoResponse;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import com.techeer.backend.global.jwt.JwtToken;
import com.techeer.backend.global.jwt.service.JwtService;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    @Operation(summary = "유저 정보")
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo() {
        User user = userService.getLoginUser();
        UserInfoResponse result = UserConverter.ofUserInfoResponse(user);
        return ResponseEntity.ok(ApiResponse.success(result, SuccessCode.USER_FETCH_OK.getMessage()));
    }

    @Operation(summary = "추가정보 입력")
    @PostMapping("/user")
    public ResponseEntity<ApiResponse<Void>> signupUser(@RequestBody @Valid SignUpRequest req) {
        userService.signup(req);
        return ResponseEntity.ok(ApiResponse.success(null, SuccessCode.USER_ADDITIONAL_INFO_OK.getMessage()));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logoutUser() {
        userService.logout();
        return ResponseEntity.ok(ApiResponse.success(null, SuccessCode.USER_LOGOUT_OK.getMessage()));
    }

    @Operation(summary = "액세스 토큰 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<Void>> reGenerateAccessToken(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                      HttpServletResponse response) throws IOException {
        // Cookie에 있는 Refresh Token을 이용하여 새로운 Access Token을 발급
        JwtToken token = userService.reissueAccessToken(refreshToken);

        // 토큰을 못 만들었으면 메인 화면으로 리다이렉트
        if(token == null) {throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);}

        // Access Token과 Refresh Token을 쿠키에 저장
        jwtService.addTokenCookies(response, token.getAccessToken(), token.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.success(null, SuccessCode.TOKEN_REISSUE_OK.getMessage()));
    }

    @Operation(summary = "모의 유저 데이터 생성")
    @PostMapping("/mock/signup")
    public ResponseEntity<ApiResponse<String>> mockSignup(@RequestParam(name = "id") String id) {
        String accessToken = userService.mockSignup(id);
        return ResponseEntity.ok(ApiResponse.success(accessToken, SuccessCode.OK.getMessage()));
    }
}
