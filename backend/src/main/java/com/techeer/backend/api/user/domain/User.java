package com.techeer.backend.api.user.domain;

import com.techeer.backend.api.user.dto.request.SignUpRequest;
import com.techeer.backend.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@Table(name = "`User`")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "refresh_token")
    private String refreshToken;

//    @Column(name = "profile_image")
//    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = true)
    private SocialType socialType;

    @Builder
    public User(String email, String username, String password, String refreshToken, Role role, SocialType socialType) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.refreshToken = refreshToken;
        this.role = role;
        this.socialType = socialType;
    }

    public void updateUser(SignUpRequest req) {
        this.username = req.getUsername();
        this.role = req.getRole();
    }

    public void onLogout() {
        this.refreshToken = null;
    }

    public String updateRefreshToken(String newRefreshToken) {
        String oldRefreshToken = this.refreshToken;
        this.refreshToken = newRefreshToken;
        return oldRefreshToken;
    }

}