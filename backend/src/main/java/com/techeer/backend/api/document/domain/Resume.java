package com.techeer.backend.api.document.domain;

import com.techeer.backend.api.file.domain.UserFile;
import com.techeer.backend.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "resumes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resume extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private UserFile file;

    @Size(max = 255)
    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Builder
    public Resume(UserFile file, String title, Boolean isDefault) {
        this.file = file;
        this.title = title;
        this.isDefault = isDefault != null ? isDefault : false;
    }

    public void updateTitle(String title) {
        if (title != null) {
            this.title = title;
        }
    }

    public void setAsDefault() {
        this.isDefault = true;
    }

    public void unsetAsDefault() {
        this.isDefault = false;
    }

}
