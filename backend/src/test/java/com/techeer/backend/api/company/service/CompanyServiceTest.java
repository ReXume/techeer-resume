package com.techeer.backend.api.company.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.company.dto.request.CompanyRegisterRequest;
import com.techeer.backend.api.company.repository.CompanyRepository;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompanyService 비즈니스 로직 테스트")
class CompanyServiceTest {

	@Mock
	private CompanyRepository companyRepository;

	@InjectMocks
	private CompanyService companyService;

	@Nested
	@DisplayName("기업 등록 테스트")
	class RegisterCompanyTest {

		@Test
		@DisplayName("성공: 새로운 기업을 정상적으로 등록한다")
		void registerCompany_Success() {
			// given
			CompanyRegisterRequest request = new CompanyRegisterRequest(
				"Techeer",
				"IT",
				"https://techeer.com",
				"Seoul"
			);

			// Mock: 이름 중복 없음
			given(companyRepository.findByName(request.name())).willReturn(Optional.empty());

			// Mock: 저장 후 ID가 설정된 Entity 반환
			Company savedCompany = Company.builder()
				.name(request.name())
				.industryDomain(request.industryDomain())
				.websiteUrl(request.websiteUrl())
				.location(request.location())
				.build();
			ReflectionTestUtils.setField(savedCompany, "id", 1L);
			given(companyRepository.save(any(Company.class))).willReturn(savedCompany);

			// when
			Long companyId = companyService.registerCompany(request);

			// then
			assertThat(companyId).isEqualTo(1L);
			verify(companyRepository).save(any(Company.class));
		}

		@Test
		@DisplayName("실패: 이미 존재하는 기업명으로 등록 시도 시 예외 발생")
		void registerCompany_Fail_DuplicateName() {
			// given
			CompanyRegisterRequest request = new CompanyRegisterRequest(
				"Techeer",
				"IT",
				"https://techeer.com",
				"Seoul"
			);

			// Mock: 이름 중복 존재
			given(companyRepository.findByName(request.name())).willReturn(Optional.of(Company.builder().build()));

			// when & then
			assertThatThrownBy(() -> companyService.registerCompany(request))
				.isInstanceOf(BusinessException.class)
				.hasFieldOrPropertyWithValue("errorCode", ErrorCode.COMPANY_ALREADY_EXISTS);
		}
	}
}
