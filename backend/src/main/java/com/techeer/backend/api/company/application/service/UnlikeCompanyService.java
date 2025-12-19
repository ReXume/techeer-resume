package com.techeer.backend.api.company.application.service;

import com.techeer.backend.api.company.application.port.in.UnlikeCompanyUseCase;
import com.techeer.backend.api.company.application.port.out.LoadCompanyLikePort;
import com.techeer.backend.api.company.domain.CompanyLike;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UnlikeCompanyService implements UnlikeCompanyUseCase {

	private final LoadCompanyLikePort loadCompanyLikePort;

	@Override
	public void unlikeCompany(Long companyLikeId, Long userId) {
		CompanyLike companyLike = loadCompanyLikePort.findById(companyLikeId)
			.orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_LIKE_NOT_FOUND));

		if (!companyLike.getUser().getId().equals(userId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}

		companyLike.softDelete();
	}

}
