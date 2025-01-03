package com.techeer.backend.api.resume.domain;

import com.techeer.backend.api.tag.company.domain.ResumeCompany;
import com.techeer.backend.api.tag.position.Position;
import com.techeer.backend.api.tag.techStack.domain.ResumeTechStack;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Resume")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor()
public class Resume extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "career")
    private Integer career;

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private Position position;

    // 이전 버전(더 오래된 버전)
    private Long previousResumeId;

    // 이후 버전(더 최신 버전)
    private Long laterResumeId;

    @Builder.Default
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    private List<ResumeTechStack> resumeTechStacks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    private List<ResumeCompany> resumeCompanies = new ArrayList<>();

    @OneToOne(mappedBy = "resume", cascade = CascadeType.ALL)
    private ResumePdf resumePdf;// S3 키 (경로)


    @Builder
    public Resume(User user, String name, int career, Position position, ResumePdf resumePdf) {
        this.user = user;
        this.name = name;
        this.career = career;
        this.position = position;
        this.resumePdf = resumePdf;
    }

    public void addResumeTechStack(ResumeTechStack resumeTechStack) {
        this.resumeTechStacks.add(resumeTechStack);
    }

    public void addResumeCompany(ResumeCompany resumeCompany) {
        this.resumeCompanies.add(resumeCompany);
    }

    public void addResumePdf(ResumePdf resumePdf) {
        this.resumePdf = resumePdf;
    }

    public void updateLaterResumeId(Long id) {
        this.laterResumeId = id;
    }
}
