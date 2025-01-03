package com.techeer.backend.global.oauth.oauth2user;

import com.techeer.backend.api.user.domain.SocialType;
import java.util.Collection;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final String email;
    private final String name;
    private final SocialType socialType;

    @Builder
    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey,
                            String email, String name, SocialType socialType) {
        super(authorities, attributes, nameAttributeKey);

        this.email = email;
        this.name = name;
        this.socialType = socialType;
    }
}