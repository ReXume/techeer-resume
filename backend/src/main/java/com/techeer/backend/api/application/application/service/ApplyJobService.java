package com.techeer.backend.api.application.application.service;

import com.techeer.backend.api.application.application.port.in.ApplyJobUseCase;
import com.techeer.backend.api.application.application.port.out.LoadApplicationPort;
import com.techeer.backend.api.application.application.port.out.SaveApplicationPort;
import com.techeer.backend.api.application.domain.Application;
import com.techeer.backend.api.application.dto.request.ApplicationApplyRequest;
import com.techeer.backend.api.job.application.port.out.LoadJobPostingPort;
import com.techeer.backend.api.job.domain.JobPosting;
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
public class ApplyJobService implements ApplyJobUseCase {

	private final SaveApplicationPort saveApplicationPort;

	private final LoadApplicationPort loadApplicationPort;

	private final LoadJobPostingPort loadJobPostingPort;

	private final LoadUserPort loadUserPort;

	@Override
	public Long applyJob(ApplicationApplyRequest request, Long userId) {
		// userId로 User 조회 (영속성 컨텍스트 1차 캐시 활용)
		// MSA 환경에서 다른 서비스 호출 시 명확한 계약
		User user = loadUserPort.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		JobPosting jobPosting = loadJobPostingPort.findById(request.jobPostingId())
			.orElseThrow(() -> new BusinessException(ErrorCode.JOB_POSTING_NOT_FOUND));

		// 중복 지원 체크
		if (loadApplicationPort.existsByUserAndJobPosting(user, jobPosting)) {
			throw new BusinessException(ErrorCode.APPLICATION_ALREADY_EXISTS);
		}

		Application application = Application.builder()
			.user(user)
			.jobPosting(jobPosting)
			.build();

		return saveApplicationPort.saveApplication(application).getId();
	}
}

