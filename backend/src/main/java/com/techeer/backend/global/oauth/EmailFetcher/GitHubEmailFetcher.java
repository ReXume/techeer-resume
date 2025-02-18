package com.techeer.backend.global.oauth.EmailFetcher;

import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

public class GitHubEmailFetcher {
    private static final String GITHUB_EMAILS_URL = "https://api.github.com/user/emails";

    private static String getPrimaryEmail(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map[]> response = restTemplate.exchange(GITHUB_EMAILS_URL, HttpMethod.GET, entity, Map[].class);
        if (response.getBody() != null) {
            for (Map<String, Object> emailInfo : response.getBody()) {
                Boolean isPrimary = (Boolean) emailInfo.get("primary");
                if (Boolean.TRUE.equals(isPrimary)) {
                    return (String) emailInfo.get("email");
                }
            }
        }
        throw new BusinessException(ErrorCode.USER_NOT_FOUND_BY_EMAIL);
    }

    // Github API 사용해서 이메일 가져오는 함수
    public static String getGitHubPrimaryEmail(OAuth2UserRequest userRequest){
        if (userRequest == null || userRequest.getAccessToken() == null) {
            throw new BusinessException(ErrorCode.USER_REQUEST_NULL);
        }
        String accessToken = userRequest.getAccessToken().getTokenValue();
        return getPrimaryEmail(accessToken);
    }


}
