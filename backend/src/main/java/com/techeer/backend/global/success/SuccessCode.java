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
	OK(HttpStatus.OK, "COMMON_200", "성공적으로 처리되었습니다."), CREATED(HttpStatus.CREATED, "COMMON_201", "성공적으로 생성되었습니다."),
	NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON_204", "성공적으로 삭제되었습니다."),

	// User Success
	USER_REGISTER_SUCCESS(HttpStatus.CREATED, "USER_201", "회원가입이 완료되었습니다."),
	USER_LOGIN_SUCCESS(HttpStatus.OK, "USER_200", "로그인에 성공했습니다."),
	USER_FETCH_OK(HttpStatus.OK, "USER_200", "유저 정보 조회 성공"),
	USER_ADDITIONAL_INFO_OK(HttpStatus.OK, "USER_201", "유저 추가정보 입력 성공"),
	USER_LOGOUT_OK(HttpStatus.OK, "USER_200", "유저 로그아웃 성공"),
	USER_PROFILE_IMAGE_UPDATE_OK(HttpStatus.OK, "USER_200", "프로필 이미지가 업데이트되었습니다."),

	// Company Success
	COMPANY_REGISTER_SUCCESS(HttpStatus.CREATED, "COMPANY_201", "기업 등록이 완료되었습니다."),

	// Token Success
	TOKEN_REISSUE_OK(HttpStatus.OK, "TOKEN_200", "토큰 재발급 성공"),

	// File Success
	FILE_UPLOAD_SUCCESS(HttpStatus.OK, "FILE_200", "파일 업로드가 완료되었습니다."),
	FILE_DELETE_SUCCESS(HttpStatus.OK, "FILE_200", "파일 삭제가 완료되었습니다."),
	FILE_DOWNLOAD_SUCCESS(HttpStatus.OK, "FILE_200", "파일 다운로드가 완료되었습니다.");

	private final HttpStatus httpStatus;

	private final String code;

	private final String message;

	@Override
	public ReasonDto getReason() {
		return ReasonDto.builder().status(httpStatus).message(message).code(code).build();
	}

}