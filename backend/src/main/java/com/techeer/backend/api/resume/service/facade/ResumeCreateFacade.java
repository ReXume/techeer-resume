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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ResumeCreateFacade {

    // service 의존관걔 주입
    private final ResumeService resumeService;
    private final CompanyService companyService;
    private final TechStackService techStackService;
    private final ResumePdfService resumePdfService;
    private final UserService userService;

    // 이력서 생성
    @Transactional
    public void createResume(CreateResumeRequest req, MultipartFile multipartFile) {
        User user = userService.getLoginUser();
        // 관련 기술 처리
        List<TechStack> techStacks = techStackService.findOrCreateTechStacks(req.getTechStackNames());

        // 타겟 회사 처리
        List<Company> companies = companyService.findOrCreateCompanies(req.getCompanyNames());

        Resume previousResume = resumeService.findLaterByUser(user);

        // 이력서 엔티티 생성
        Resume resume = Resume.builder()
                .user(user)
                .position(req.getPosition())
                .career(req.getCareer())
                .name("Resume of " + user.getUsername() + " - " + LocalDate.now(ZoneId.of("Asia/Seoul")))
                .previousResumeId(previousResume != null ? previousResume.getId() : null)
                .build();

        resumeService.saveResume(resume);

        if (previousResume != null) {
            previousResume.updateLaterResumeId(resume.getId());
        }

        // ResumeTechStack, ResumeCompany 생성 및 이력서에 추가
        addResumeTechStacks(resume, techStacks);
        addResumeCompanies(resume, companies);

        ResumePdf resumePdf = resumePdfService.saveResumePdf(resume, multipartFile);
        resume.addResumePdf(resumePdf);

        user.addResume(resume);
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
