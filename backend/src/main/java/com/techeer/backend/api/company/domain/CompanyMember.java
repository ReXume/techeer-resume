package com.techeer.backend.api.company.domain;

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
@Table(name = "company_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", nullable = false)
	private Company company;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private CompanyRole role;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private MemberStatus status = MemberStatus.ACTIVE;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Builder
	public CompanyMember(User user, Company company, CompanyRole role, MemberStatus status) {
		this.user = user;
		this.company = company;
		this.role = role;
		this.status = status != null ? status : MemberStatus.ACTIVE;
	}

	public void updateRole(CompanyRole role) {
		if (role != null) {
			this.role = role;
		}
	}

	public void updateStatus(MemberStatus status) {
		if (status != null) {
			this.status = status;
		}
	}

	public void activate() {
		this.status = MemberStatus.ACTIVE;
	}

	public void deactivate() {
		this.status = MemberStatus.INACTIVE;
	}

	public void setPending() {
		this.status = MemberStatus.PENDING;
	}

}
