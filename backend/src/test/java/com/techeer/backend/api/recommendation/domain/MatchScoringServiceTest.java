package com.techeer.backend.api.recommendation.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.techeer.backend.api.recommendation.domain.vo.MatchScore;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MatchScoringServiceTest {

	private MatchScoringService service;

	@BeforeEach
	void setUp() {
		service = new MatchScoringService();
	}

	@Test
	@DisplayName("skillOverlap: 모든 스킬 일치 시 1.0")
	void skillOverlap_allMatch() {
		List<String> userSkills = List.of("Java", "Spring", "MySQL");
		List<String> jobSkills = List.of("Java", "Spring");
		double result = service.calculateSkillOverlap(userSkills, jobSkills);
		assertThat(result).isEqualTo(1.0);
	}

	@Test
	@DisplayName("skillOverlap: 절반 일치 시 0.5")
	void skillOverlap_halfMatch() {
		List<String> userSkills = List.of("Java");
		List<String> jobSkills = List.of("Java", "Spring");
		double result = service.calculateSkillOverlap(userSkills, jobSkills);
		assertThat(result).isEqualTo(0.5);
	}

	@Test
	@DisplayName("skillOverlap: 필요 스킬 없으면 0.0")
	void skillOverlap_noRequiredSkills() {
		double result = service.calculateSkillOverlap(List.of("Java"), List.of());
		assertThat(result).isEqualTo(0.0);
	}

	@Test
	@DisplayName("skillOverlap: 대소문자 무관 일치")
	void skillOverlap_caseInsensitive() {
		List<String> userSkills = List.of("java", "SPRING");
		List<String> jobSkills = List.of("Java", "Spring");
		double result = service.calculateSkillOverlap(userSkills, jobSkills);
		assertThat(result).isEqualTo(1.0);
	}

	@Test
	@DisplayName("experienceMatch: 동일 레벨 1.0")
	void experienceMatch_exact() {
		double result = service.calculateExperienceMatch("MID", "MID");
		assertThat(result).isEqualTo(1.0);
	}

	@Test
	@DisplayName("experienceMatch: 인접 레벨 0.5")
	void experienceMatch_adjacent() {
		double result = service.calculateExperienceMatch("JUNIOR", "MID");
		assertThat(result).isEqualTo(0.5);
	}

	@Test
	@DisplayName("experienceMatch: 멀리 떨어진 레벨 0.0")
	void experienceMatch_miss() {
		double result = service.calculateExperienceMatch("JUNIOR", "SENIOR");
		assertThat(result).isEqualTo(0.0);
	}

	@Test
	@DisplayName("positionMatch: 동일 포지션 1.0")
	void positionMatch_exact() {
		double result = service.calculatePositionMatch("Backend Developer", "Backend Developer");
		assertThat(result).isEqualTo(1.0);
	}

	@Test
	@DisplayName("positionMatch: 다른 포지션 0.0")
	void positionMatch_different() {
		double result = service.calculatePositionMatch("Backend Developer", "Frontend Developer");
		assertThat(result).isEqualTo(0.0);
	}

	@Test
	@DisplayName("locationMatch: 포함되면 1.0")
	void locationMatch_contains() {
		double result = service.calculateLocationMatch(List.of("서울"), "서울 강남구");
		assertThat(result).isEqualTo(1.0);
	}

	@Test
	@DisplayName("locationMatch: 포함 안 되면 0.0")
	void locationMatch_noMatch() {
		double result = service.calculateLocationMatch(List.of("부산"), "서울 강남구");
		assertThat(result).isEqualTo(0.0);
	}

	@Test
	@DisplayName("recencyBonus: 오늘 게시된 공고는 1.0")
	void recencyBonus_today() {
		double result = service.calculateRecencyBonus(LocalDateTime.now());
		assertThat(result).isCloseTo(1.0, within(0.01));
	}

	@Test
	@DisplayName("recencyBonus: 30일 이상 된 공고는 0.0")
	void recencyBonus_expired() {
		double result = service.calculateRecencyBonus(LocalDateTime.now().minusDays(30));
		assertThat(result).isEqualTo(0.0);
	}

	@Test
	@DisplayName("recencyBonus: 15일 된 공고는 약 0.5")
	void recencyBonus_halfway() {
		double result = service.calculateRecencyBonus(LocalDateTime.now().minusDays(15));
		assertThat(result).isCloseTo(0.5, within(0.05));
	}

	@Test
	@DisplayName("총 점수 계산: 모든 항목 완벽 일치 시 1.0에 근접")
	void calculate_perfectMatch() {
		MatchScore score = service.calculate(
			List.of("Java", "Spring"),
			"MID",
			"Backend Developer",
			List.of("서울"),
			List.of("Java", "Spring"),
			"MID",
			"Backend Developer",
			"서울 강남구",
			LocalDateTime.now()
		);
		// 0.40*1.0 + 0.25*1.0 + 0.20*1.0 + 0.10*1.0 + 0.05*1.0 = 1.0
		assertThat(score.getValue()).isCloseTo(1.0, within(0.05));
	}

	@Test
	@DisplayName("총 점수 계산: 스킬만 일치 시 0.40")
	void calculate_skillOnlyMatch() {
		MatchScore score = service.calculate(
			List.of("Java"),
			"JUNIOR",
			"Backend Developer",
			List.of("서울"),
			List.of("Java"),
			"SENIOR",
			"Frontend Developer",
			"부산",
			LocalDateTime.now().minusDays(30)
		);
		// skill=1.0, exp=0.0, pos=0.0, loc=0.0, recency=0.0 → 0.40
		assertThat(score.getValue()).isCloseTo(0.40, within(0.01));
	}

	@Test
	@DisplayName("MatchScore 범위 검증: 0.0 이하 값 입력 시 예외")
	void matchScore_invalidRange() {
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
			() -> new com.techeer.backend.api.recommendation.domain.vo.MatchScore(-0.1, 0.0, 0.0, 0.0, 0.0, 0.0));
	}

}
