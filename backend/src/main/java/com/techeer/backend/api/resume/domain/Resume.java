package com.techeer.backend.api.resume.domain;

import com.techeer.backend.api.file.domain.UserFile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "resumes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resume {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "resume_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "file_id", nullable = false)
	private UserFile file;

	@Column(name = "title", length = 255)
	private String title;

	@Column(name = "is_default")
	private Boolean isDefault = false;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

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
