package com.techeer.backend.global.success;

import com.techeer.backend.global.common.response.ReasonDto;
import com.techeer.backend.global.common.status.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseStatus {
    // Common Success
    OK(HttpStatus.OK, "COMMON_200", "성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "COMMON_201", "성공적으로 생성되었습니다."),
    NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON_204", "성공적으로 삭제되었습니다."),

    // Resume Success
    RESUME_CREATED(HttpStatus.CREATED, "RESUME_201", "이력서가 성공적으로 저장되었습니다"),
    RESUME_FETCH_OK(HttpStatus.OK, "RESUME_200", "이력서 조회 성공"),

    // User  Success
    USER_FETCH_OK(HttpStatus.OK, "USER_200", "유저 정보 조회 성공"),
    USER_ADDITIONAL_INFO_OK(HttpStatus.OK, "USER_201", "유저 추가정보 입력 성공"),
    USER_LOGOUT_OK(HttpStatus.OK, "USER_200", "유저 로그아웃 성공"),

    // Token Success
    TOKEN_REISSUE_OK(HttpStatus.OK, "TOKEN_200", "토큰 재발급 성공"),

    // Feedback Success
    FEEDBACK_FETCH_OK(HttpStatus.OK, "FEEDBACK_200", "피드백 조회 성공"),

    // AIFeedback Success
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .status(httpStatus)
                .message(message)
                .code(code)
                .build();
    }
}