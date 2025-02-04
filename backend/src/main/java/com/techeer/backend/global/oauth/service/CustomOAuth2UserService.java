package com.techeer.backend.global.oauth.service;

import com.techeer.backend.api.user.domain.SocialType;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.oauth.EmailFetcher.GitHubEmailFetcher;
import com.techeer.backend.global.oauth.OAuthAttributes;
import com.techeer.backend.global.oauth.oauth2user.CustomOAuth2User;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    final private UserRepository userRepository;
    final private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuthAttributes extractAttributes;
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // OAuth2 로그인 시 키(PK)가 되는 값
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 소셜 로그인에서 API가 제공하는 userInfo의 Json 값(유저 정보들)
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        extractAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributes);

        String email= extractAttributes.getSocialType().equals(SocialType.GITHUB) ? GitHubEmailFetcher.getGitHubPrimaryEmail(userRequest)
                : (String) attributes.get("email");
        String username = extractAttributes.getSocialType().equals(SocialType.GITHUB) ? (String) attributes.get("login")
                : (String) attributes.get("name");

        if (userRepository.findByEmailAndSocialType(email, extractAttributes.getSocialType()).isEmpty()) {
            userService.CreateRegularUser(email, username, extractAttributes.getSocialType());
        }

        // DefaultOAuth2User를 구현한 CustomOAuth2User 객체를 생성해서 반환
        return CustomOAuth2User.builder()
                .authorities(Collections.emptyList())
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .name(username)
                .email(email)
                .socialType(extractAttributes.getSocialType())
                .build();
    }

    private String emailExtractor(Map<String, Object> attributes, String registrationId) {
        if (attributes.get("email") == null) {
            throw new OAuth2AuthenticationException("email is null");
        }


        return null;
    }
}