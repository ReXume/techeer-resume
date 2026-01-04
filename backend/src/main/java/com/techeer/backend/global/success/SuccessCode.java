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
	COMPANY_UPDATE_SUCCESS(HttpStatus.OK, "COMPANY_200", "기업 정보가 수정되었습니다."),
	COMPANY_DELETE_SUCCESS(HttpStatus.OK, "COMPANY_200", "기업이 삭제되었습니다."),

	// JobPosting Success
	JOB_POSTING_CREATE_SUCCESS(HttpStatus.CREATED, "JOB_201", "채용공고가 등록되었습니다."),
	JOB_POSTING_UPDATE_SUCCESS(HttpStatus.OK, "JOB_200", "채용공고가 수정되었습니다."),
	JOB_POSTING_DELETE_SUCCESS(HttpStatus.OK, "JOB_200", "채용공고가 삭제되었습니다."),

	// Resume Success
	RESUME_CREATE_SUCCESS(HttpStatus.CREATED, "RESUME_201", "이력서가 등록되었습니다."),
	RESUME_GET_SUCCESS(HttpStatus.OK, "RESUME_200", "이력서 조회에 성공했습니다."),
	RESUME_UPDATE_SUCCESS(HttpStatus.OK, "RESUME_200", "이력서가 수정되었습니다."),
	RESUME_DELETE_SUCCESS(HttpStatus.OK, "RESUME_200", "이력서가 삭제되었습니다."),

	// Portfolio Success
	PORTFOLIO_CREATE_SUCCESS(HttpStatus.CREATED, "PORTFOLIO_201", "포트폴리오가 등록되었습니다."),
	PORTFOLIO_GET_SUCCESS(HttpStatus.OK, "PORTFOLIO_200", "포트폴리오 조회에 성공했습니다."),
	PORTFOLIO_UPDATE_SUCCESS(HttpStatus.OK, "PORTFOLIO_200", "포트폴리오가 수정되었습니다."),
	PORTFOLIO_DELETE_SUCCESS(HttpStatus.OK, "PORTFOLIO_200", "포트폴리오가 삭제되었습니다."),

	// Education Success
	EDUCATION_CREATE_SUCCESS(HttpStatus.CREATED, "EDUCATION_201", "학력이 등록되었습니다."),
	EDUCATION_GET_SUCCESS(HttpStatus.OK, "EDUCATION_200", "학력 조회에 성공했습니다."),
	EDUCATION_UPDATE_SUCCESS(HttpStatus.OK, "EDUCATION_200", "학력이 수정되었습니다."),
	EDUCATION_DELETE_SUCCESS(HttpStatus.OK, "EDUCATION_200", "학력이 삭제되었습니다."),

	// Application Success
	APPLICATION_APPLY_SUCCESS(HttpStatus.CREATED, "APPLICATION_201", "채용공고에 지원하였습니다."),
	APPLICATION_CANCEL_SUCCESS(HttpStatus.OK, "APPLICATION_200", "지원이 취소되었습니다."),

	// Bookmark Success
	BOOKMARK_CREATE_SUCCESS(HttpStatus.CREATED, "BOOKMARK_201", "채용공고를 북마크했습니다."),
	BOOKMARK_GET_SUCCESS(HttpStatus.OK, "BOOKMARK_200", "북마크 조회에 성공했습니다."),
	BOOKMARK_CANCEL_SUCCESS(HttpStatus.OK, "BOOKMARK_200", "북마크가 취소되었습니다."),

	// Company Like Success
	COMPANY_LIKE_CREATE_SUCCESS(HttpStatus.CREATED, "COMPANY_LIKE_201", "기업을 좋아요했습니다."),
	COMPANY_LIKE_CANCEL_SUCCESS(HttpStatus.OK, "COMPANY_LIKE_200", "좋아요가 취소되었습니다."),

	// UserCareer Success
	USER_CAREER_CREATE_SUCCESS(HttpStatus.CREATED, "CAREER_201", "경력이 등록되었습니다."),
	USER_CAREER_UPDATE_SUCCESS(HttpStatus.OK, "CAREER_200", "경력이 수정되었습니다."),
	USER_CAREER_DELETE_SUCCESS(HttpStatus.OK, "CAREER_200", "경력이 삭제되었습니다."),

	// Skill Success
	SKILL_CREATE_SUCCESS(HttpStatus.CREATED, "SKILL_201", "기술 스택이 등록되었습니다."),
	SKILL_GET_SUCCESS(HttpStatus.OK, "SKILL_200", "기술 스택 조회에 성공했습니다."),
	SKILL_UPDATE_SUCCESS(HttpStatus.OK, "SKILL_200", "기술 스택이 수정되었습니다."),
	SKILL_DELETE_SUCCESS(HttpStatus.OK, "SKILL_200", "기술 스택이 삭제되었습니다."),

	// UserSkill Success
	USER_SKILL_CREATE_SUCCESS(HttpStatus.CREATED, "SKILL_201", "스킬이 등록되었습니다."),
	USER_SKILL_DELETE_SUCCESS(HttpStatus.OK, "SKILL_200", "스킬이 삭제되었습니다."),

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
