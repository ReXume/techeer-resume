package com.techeer.backend.api.resume.service.facade;

import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.domain.ResumePdf;
import com.techeer.backend.api.resume.dto.request.CreateResumeRequest;
import com.techeer.backend.api.resume.service.ResumePdfService;
import com.techeer.backend.api.resume.service.ResumeService;
import com.techeer.backend.api.tag.company.domain.Company;
import com.techeer.backend.api.tag.company.domain.ResumeCompany;
import com.techeer.backend.api.tag.company.service.CompanyService;
import com.techeer.backend.api.tag.techStack.domain.ResumeTechStack;
import com.techeer.backend.api.tag.techStack.domain.TechStack;
import com.techeer.backend.api.tag.techStack.service.TechStackService;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import com.techeer.backend.global.lock.DistributedLockService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ResumeCreateFacade {

    private final ResumeService resumeService;
    private final CompanyService companyService;
    private final TechStackService techStackService;
    private final ResumePdfService resumePdfService;
    private final UserService userService;
    private final RedissonClient redissonClient;
    private final DistributedLockService distributedLockService;

    @Transactional
    public void createResume(CreateResumeRequest req, MultipartFile multipartFile) {
        // 0) 현재 로그인 유저 가져오기
        User user = userService.getLoginUser();
        Long userId = user.getId();

        // 1) 분산 락 획득 (동시에 들어오는 요청 방지)
        String lockKey = "resume-create-lock:" + userId;
        RLock lock = distributedLockService.acquireLock(lockKey, 0, 10, TimeUnit.SECONDS);
        if (lock == null) {
            // 락 획득 실패 시 (이미 다른 요청이 진행 중)
            throw new BusinessException(ErrorCode.DUPLICATE_REQUEST);
        }

        try {
            // 2) 1분 내 재등록 여부 확인 (Redis에 최근 등록 시각 저장)
            RBucket<Long> lastCreatedBucket = redissonClient.getBucket("resume:lastCreated:" + userId);
            Long lastCreatedTime = lastCreatedBucket.get();

            if (lastCreatedTime != null &&
                    (System.currentTimeMillis() - lastCreatedTime) < 60_000) {
                throw new BusinessException(ErrorCode.TOO_OFTEN_REQUEST);
            }

            // (a) 태그 및 회사 조회/생성
            List<TechStack> techStacks = techStackService.findOrCreateTechStacks(req.getTechStackNames());
            List<Company> companies = companyService.findOrCreateCompanies(req.getCompanyNames());

            // (b) 이전(가장 최근) 이력서 조회
            Resume previousResume = resumeService.findLatestByUser(user);

            // (c) 새 Resume 엔티티 생성
            Resume resume = Resume.builder()
                    .user(user)
                    .position(req.getPosition())
                    .career(req.getCareer())
                    .name("Resume of " + user.getUsername() + " - "
                            + LocalDate.now(ZoneId.of("Asia/Seoul")))
                    .previousResumeId(previousResume != null ? previousResume.getId() : null)
                    .viewCount(0L)
                    .build();

            resumeService.saveResume(resume);

            if (previousResume != null) {
                previousResume.updateLaterResumeId(resume.getId());
            }

            // (d) ResumeTechStack, ResumeCompany 연결
            addResumeTechStacks(resume, techStacks);
            addResumeCompanies(resume, companies);

            // (e) PDF 저장 (파일이 존재할 경우)
            if (multipartFile != null && !multipartFile.isEmpty()) {
                ResumePdf resumePdf = resumePdfService.saveResumePdf(resume, multipartFile);
                resume.addResumePdf(resumePdf);
            }

            // (f) 유저에 이력서 추가
            user.addResume(resume);

            // 4) 등록 성공 시 -> "최근 등록 시각" 저장 (TTL=60초)
            lastCreatedBucket.set(System.currentTimeMillis(), 60, TimeUnit.SECONDS);

        } finally {
            // 5) 락 해제
            distributedLockService.releaseLock(lock);
        }
    }

    private void addResumeTechStacks(Resume resume, List<TechStack> techStacks) {
        techStacks.forEach(techStack -> {
            ResumeTechStack resumeTechStack = new ResumeTechStack(resume, techStack);
            resume.addResumeTechStack(resumeTechStack);
        });
    }

    private void addResumeCompanies(Resume resume, List<Company> companies) {
        companies.forEach(company -> {
            ResumeCompany resumeCompany = new ResumeCompany(resume, company);
            resume.addResumeCompany(resumeCompany);
        });
    }
}
