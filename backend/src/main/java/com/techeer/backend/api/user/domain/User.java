package com.techeer.backend.api.user.domain;


import com.techeer.backend.api.bookmark.domain.Bookmark;
import com.techeer.backend.api.feedback.domain.Feedback;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.user.dto.request.SignUpRequest;
import com.techeer.backend.global.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "refresh_token")
    private String refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resume> resumes = new ArrayList<>();
//    @Column(name = "profile_image")
//    private String profileImage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type")
    private SocialType socialType;

    @Builder
    public User(String email, String username, String refreshToken, Role role, SocialType socialType) {
        this.email = email;
        this.username = username;
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

    public void updateRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }

    public void addResume(Resume resume) {
        this.resumes.add(resume);
    }


}