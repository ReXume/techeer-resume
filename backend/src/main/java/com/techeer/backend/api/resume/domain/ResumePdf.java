package com.techeer.backend.api.resume.domain;

import com.techeer.backend.global.vo.Pdf;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@Entity
@Table(name = "resume_pdf")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumePdf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_pdf_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "pdfUrl", column = @Column(name = "resume_pdf_url")),
            @AttributeOverride(name = "pdfName", column = @Column(name = "resume_pdf_name")),
            @AttributeOverride(name = "pdfUUID", column = @Column(name = "resume_pdf_uuid"))
    })
    private Pdf pdf;

    @Builder
    public ResumePdf(Resume resume, Pdf pdf) {
        this.resume = resume;
        this.pdf = pdf;
    }
}
