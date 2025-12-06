package com.techeer.backend.api.user.domain;

import com.techeer.backend.api.user.dto.request.SignUpRequest;
import com.techeer.backend.global.common.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@NotNull
	@Email
	@Size(max = 255)
	@Column(name = "email", nullable = false, unique = true, length = 255)
	private String email;

	@Size(max = 50)
	@Column(name = "name", length = 50)
	private String name;

	@Size(max = 255)
	@Column(name = "password", length = 255)
	private String password;

	@Size(max = 500)
	@Column(name = "refresh_token", length = 500)
	private String refreshToken;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "fileUrl",
					column = @Column(name = "profile_image_url", columnDefinition = "TEXT")),
			@AttributeOverride(name = "fileType", column = @Column(name = "profile_image_type")),
			@AttributeOverride(name = "fileName", column = @Column(name = "profile_image_name")),
			@AttributeOverride(name = "fileUUID", column = @Column(name = "profile_image_uuid")) })
	private File profileImage;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 20)
	private Role role = Role.USER;

	@Enumerated(EnumType.STRING)
	@Column(name = "social_type", nullable = false, length = 20)
	private SocialType socialType;

	@Builder
	public User(String email, String name, String password, String refreshToken, File profileImage, Role role,
			SocialType socialType) {
		this.email = email;
		this.name = name;
		this.password = password;
		this.refreshToken = refreshToken;
		this.profileImage = profileImage;
		this.role = role != null ? role : Role.USER;
		this.socialType = socialType;
	}

	public void updateUser(SignUpRequest req) {
		this.name = req.name();
		this.role = req.role();
	}

	public void updateName(String name) {
		if (name != null) {
			this.name = name;
		}
	}

	public void updateRole(Role role) {
		if (role != null) {
			this.role = role;
		}
	}

	public void onLogout() {
		this.refreshToken = null;
	}

	public String updateRefreshToken(String newRefreshToken) {
		String oldRefreshToken = this.refreshToken;
		this.refreshToken = newRefreshToken;
		return oldRefreshToken;
	}

	public void updateProfileImage(File profileImage) {
		this.profileImage = profileImage;
	}

}