package com.techeer.backend.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// Common Error
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러입니다. 관리자에게 문의하세요."),
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "금지된 요청입니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "찾을 수 없는 요청입니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_405", "허용되지 않은 메소드입니다."),
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
	INPUT_VALUE_INVALID(HttpStatus.BAD_REQUEST, "REQUEST_400", "요청사항에 필수 인자가 누락되었습니다"),
	HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "G005", "request message body가 없거나, 값 타입이 올바르지 않습니다."),

	// User Error
	USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "USER_401", "로그인 정보가 없습니다."),
	USER_NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "USER_401", "로그인 하지 않았습니다."),
	USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_409", "이미 존재하는 이메일입니다."),
	USER_PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "USER_401", "비밀번호가 일치하지 않습니다."),
	USER_SOCIAL_LOGIN_ONLY(HttpStatus.BAD_REQUEST, "USER_400", "소셜 로그인 사용자는 자체 로그인을 사용할 수 없습니다."),

	// Token Error
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_401", "리프레시 토큰이 유효하지 않습니다."),

	// Company Error
	COMPANY_ALREADY_EXISTS(HttpStatus.CONFLICT, "COMPANY_409", "이미 존재하는 기업입니다."),
	COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPANY_404", "기업을 찾을 수 없습니다."),

	// JobPosting Error
	JOB_POSTING_NOT_FOUND(HttpStatus.NOT_FOUND, "JOB_404", "채용공고를 찾을 수 없습니다."),

	// Resume Error
	RESUME_NOT_FOUND(HttpStatus.NOT_FOUND, "RESUME_404", "이력서를 찾을 수 없습니다."),

	// File Upload Error
	FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_500", "파일 업로드 중 오류가 발생했습니다."),
	FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_500", "파일 삭제 중 오류가 발생했습니다."),
	FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE_404", "파일을 찾을 수 없습니다."),
	INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "FILE_400", "지원하지 않는 파일 형식입니다."),
	FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "FILE_400", "파일 크기가 제한을 초과했습니다.");

	private final HttpStatus httpStatus;

	private final String code;

	private final String message;

}
