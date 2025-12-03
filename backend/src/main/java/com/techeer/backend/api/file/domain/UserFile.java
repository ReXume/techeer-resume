package com.techeer.backend.api.file.domain;

import com.techeer.backend.api.user.domain.FileType;
import com.techeer.backend.api.user.domain.User;
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
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_files")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "file_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false, length = 50)
	private FileCategory category;

	@Column(name = "uuid", nullable = false, length = 36, unique = true)
	private String uuid;

	@Column(name = "file_url", nullable = false, length = 2083)
	private String fileUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "file_type", length = 50)
	private FileType fileType;

	@Column(name = "original_name", length = 255)
	private String originalName;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Builder
	public UserFile(User user, FileCategory category, String uuid, String fileUrl, FileType fileType,
			String originalName) {
		this.user = user;
		this.category = category;
		this.uuid = uuid;
		this.fileUrl = fileUrl;
		this.fileType = fileType;
		this.originalName = originalName;
	}

	public void updateFileInfo(String fileUrl, FileType fileType, String originalName) {
		if (fileUrl != null) {
			this.fileUrl = fileUrl;
		}
		if (fileType != null) {
			this.fileType = fileType;
		}
		if (originalName != null) {
			this.originalName = originalName;
		}
	}

}
