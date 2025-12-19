package com.techeer.backend.api.user.domain;

import lombok.ToString;

@ToString
public enum SocialType {

	LOCAL, // 자체 회원가입
	GITHUB, GOOGLE, KAKAO, LINKEDIN

}
