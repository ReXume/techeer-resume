package com.techeer.backend.api.skill.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "skills")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Skill {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "skill_id")
	private Long id;

	@NotNull
	@Size(max = 100)
	@Column(name = "name", nullable = false, length = 100, unique = true)
	private String name;

	@Builder
	public Skill(String name) {
		this.name = name;
	}

	public void updateName(String name) {
		if (name != null) {
			this.name = name;
		}
	}

}
