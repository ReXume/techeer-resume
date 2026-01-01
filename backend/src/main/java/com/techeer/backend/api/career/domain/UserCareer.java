package com.techeer.backend.api.career.domain;

import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.file.domain.UserFile;
import com.techeer.backend.api.user.domain.User;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_careers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCareer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private UserFile file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @NotNull
    @Size(max = 100)
    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Size(max = 100)
    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "is_current")
    private Boolean isCurrent;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public UserCareer(User user, UserFile file, Company company, String companyName, String jobTitle, Boolean isCurrent,
                      LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.file = file;
        this.company = company;
        this.companyName = companyName;
        this.jobTitle = jobTitle;
        this.isCurrent = isCurrent;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateCareer(String companyName, String jobTitle, Boolean isCurrent, LocalDate startDate,
                             LocalDate endDate) {
        if (companyName != null) {
            this.companyName = companyName;
        }
        if (jobTitle != null) {
            this.jobTitle = jobTitle;
        }
        if (isCurrent != null) {
            this.isCurrent = isCurrent;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
    }

    public void endCareer(LocalDate endDate) {
        this.isCurrent = false;
        this.endDate = endDate;
    }

}
