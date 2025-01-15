package com.techeer.backend.api.user.controller;

import com.techeer.backend.api.user.converter.UserConverter;
import com.techeer.backend.api.user.dto.request.SignUpRequest;
import com.techeer.backend.api.user.dto.response.UserInfoResponse;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.common.response.CommonResponse;
import com.techeer.backend.global.jwt.JwtToken;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    @Operation(summary = "유저 정보")
    @GetMapping("/user")
    public CommonResponse<UserInfoResponse> getUserInfo() {
        UserInfoResponse result = UserConverter.ofUserInfoResponse(userService.getLoginUser());
        return CommonResponse.of(SuccessCode.USER_FETCH_OK, result);
    }

    @Operation(summary = "추가정보 입력")
    @PostMapping("/user")
    public CommonResponse<Void> signupUser(@RequestBody @Valid SignUpRequest req) {
        userService.signup(req);
        return CommonResponse.of(SuccessCode.USER_ADDITIONAL_INFO_OK, null);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public CommonResponse<Void> logoutUser() {
        userService.logout();
        return CommonResponse.of(SuccessCode.USER_LOGOUT_OK, null);
    }

    @Operation(summary = "액세스 토큰 재발급")
    @PostMapping("/reissue")
    public CommonResponse<Void> reGenerateAccessToken(@RequestHeader("Access-Token") String accessToken,
                                                      @RequestHeader("Refresh-Token") String refreshToken,
                                                      HttpServletResponse response) {
        JwtToken result = userService.reissueToken(accessToken, refreshToken);
        response.setHeader("Access-Token", result.getAccessToken());
        response.setHeader("Refresh-Token", result.getRefreshToken());
        return CommonResponse.of(SuccessCode.TOKEN_REISSUE_OK, null);
    }

    @Operation(summary = "모의 유저 데이터 생성")
    @PostMapping("/mock/signup")
    public CommonResponse<String> mockSignup(@RequestParam(name = "id") String id) {
        String accessToken = userService.mockSignup(id);
        return CommonResponse.of(SuccessCode.OK, accessToken);
    }
}
