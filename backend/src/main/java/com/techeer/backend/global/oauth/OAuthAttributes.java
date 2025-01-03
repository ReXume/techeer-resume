package com.techeer.backend.global.oauth;

import com.techeer.backend.api.user.domain.SocialType;
import com.techeer.backend.global.oauth.oauth2user.GitHubOAuth2UserInfo;
import com.techeer.backend.global.oauth.oauth2user.GoogleOAuth2UserInfo;
import com.techeer.backend.global.oauth.oauth2user.OAuth2UserInfo;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {

    private final String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private final OAuth2UserInfo oauth2UserInfo; // 소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)
    private final SocialType socialType;

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo, SocialType socialType) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
        this.socialType = socialType;
    }

    // OAuth2User에서 반환하는 사용자 정보는 Map
    // 따라서 값 하나하나를 변환해야 한다.
    public static OAuthAttributes of(String registrationId,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return ofGoogle(userNameAttributeName, attributes);
        }
        return ofGithub(userNameAttributeName, attributes);
    }

    // 구글 생성자
    private static OAuthAttributes ofGoogle(String usernameAttributeName,
                                            Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(usernameAttributeName)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .socialType(SocialType.GOOGLE)
                .build();
    }

    // 깃허브 생성자
    private static OAuthAttributes ofGithub(String usernameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(usernameAttributeName)
                .oauth2UserInfo(new GitHubOAuth2UserInfo(attributes))
                .socialType(SocialType.GITHUB)
                .build();
    }

}