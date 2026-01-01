package com.techeer.backend.api.company.application.service;

import com.techeer.backend.api.company.application.port.in.RegisterCompanyUseCase;
import com.techeer.backend.api.company.application.port.out.LoadCompanyPort;
import com.techeer.backend.api.company.application.port.out.SaveCompanyMemberPort;
import com.techeer.backend.api.company.application.port.out.SaveCompanyPort;
import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.company.domain.CompanyMember;
import com.techeer.backend.api.company.domain.CompanyRole;
import com.techeer.backend.api.company.dto.request.CompanyRegisterRequest;
import com.techeer.backend.api.user.application.port.out.LoadUserPort;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterCompanyService implements RegisterCompanyUseCase {

	private final LoadCompanyPort loadCompanyPort;

	private final SaveCompanyPort saveCompanyPort;

	private final SaveCompanyMemberPort saveCompanyMemberPort;

	private final LoadUserPort loadUserPort;

	@Override
	public Long registerCompany(CompanyRegisterRequest request, Long userId) {
		// 1. 기업명 중복 체크
		if (loadCompanyPort.findByName(request.name()).isPresent()) {
			throw new BusinessException(ErrorCode.COMPANY_ALREADY_EXISTS);
		}

		// 2. 사용자 조회
		User user = loadUserPort.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		// 3. 기업 생성
		// NOTE: 회사 이메일 검증은 별도 인증 서버(멀티모듈)에서 처리 예정
		Company company = Company.builder()
				.name(request.name())
				.industryDomain(request.industryDomain())
				.websiteUrl(request.websiteUrl())
				.location(request.location())
				.build();

		Company savedCompany = saveCompanyPort.saveCompany(company);

		// 4. 기업 생성자를 관리자로 등록
		CompanyMember adminMember = CompanyMember.builder()
				.user(user)
				.company(savedCompany)
				.role(CompanyRole.ADMIN)
				.build();

		saveCompanyMemberPort.saveCompanyMember(adminMember);

		log.info("Company registered successfully. companyId={}, companyName={}, adminUserId={}", savedCompany.getId(),
				savedCompany.getName(), userId);

		return savedCompany.getId();
	}

}
