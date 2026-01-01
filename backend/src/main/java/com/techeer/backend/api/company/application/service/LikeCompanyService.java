package com.techeer.backend.api.company.application.service;

import com.techeer.backend.api.company.application.port.in.LikeCompanyUseCase;
import com.techeer.backend.api.company.application.port.out.LoadCompanyLikePort;
import com.techeer.backend.api.company.application.port.out.LoadCompanyPort;
import com.techeer.backend.api.company.application.port.out.SaveCompanyLikePort;
import com.techeer.backend.api.company.domain.Company;
import com.techeer.backend.api.company.domain.CompanyLike;
import com.techeer.backend.api.company.dto.request.CompanyLikeCreateRequest;
import com.techeer.backend.api.user.application.port.out.LoadUserPort;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeCompanyService implements LikeCompanyUseCase {

	private final SaveCompanyLikePort saveCompanyLikePort;

	private final LoadCompanyLikePort loadCompanyLikePort;

	private final LoadCompanyPort loadCompanyPort;

	private final LoadUserPort loadUserPort;

	@Override
	public Long likeCompany(CompanyLikeCreateRequest request, Long userId) {
		User user = loadUserPort.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		Company company = loadCompanyPort.findById(request.companyId())
				.orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

		// 중복 좋아요 체크
		if (loadCompanyLikePort.existsByUserAndCompany(user, company)) {
			throw new BusinessException(ErrorCode.COMPANY_LIKE_ALREADY_EXISTS);
		}

		CompanyLike companyLike = CompanyLike.builder().user(user).company(company).build();

		return saveCompanyLikePort.saveCompanyLike(companyLike).getId();
	}

}
