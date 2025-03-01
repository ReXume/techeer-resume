package com.techeer.backend.api.resume.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.techeer.backend.api.resume.domain.QResume;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.dto.request.ResumeSearchRequest;
import com.techeer.backend.api.tag.company.domain.QCompany;
import com.techeer.backend.api.tag.company.domain.QResumeCompany;
import com.techeer.backend.api.tag.position.Position;
import com.techeer.backend.api.tag.techStack.domain.QResumeTechStack;
import com.techeer.backend.api.tag.techStack.domain.QTechStack;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ResumeRepositoryQueryDslImpl implements ResumeRepositoryQueryDsl {

    private final JPAQueryFactory queryFactory;

    public ResumeRepositoryQueryDslImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Resume> searchByCriteria(ResumeSearchRequest req, Pageable pageable) {
        QResume resume = QResume.resume;
        QResumeTechStack resumeTechStack = QResumeTechStack.resumeTechStack;
        QTechStack techStack = QTechStack.techStack;
        QResumeCompany resumeCompany = QResumeCompany.resumeCompany;
        QCompany company = QCompany.company;

        // 필터 조건 생성
        BooleanBuilder builder = new BooleanBuilder();

        // 경력 필터
        if (req.getMinCareer() != null && req.getMaxCareer() != null) {
            builder.and(resume.career.between(req.getMinCareer(), req.getMaxCareer()));
        } else if (req.getMinCareer() != null) {
            builder.and(resume.career.goe(req.getMinCareer()));
        } else if (req.getMaxCareer() != null) {
            builder.and(resume.career.loe(req.getMaxCareer()));
        }

        // Position 필터
        List<Position> positions = req.getPositions();
        if (positions != null && !positions.isEmpty()) {
            builder.and(resume.position.in(positions));
        }

        // TechStack 이름 필터
        List<String> techStackNames = req.getTechStackNames();
        if (techStackNames != null && !techStackNames.isEmpty()) {
            builder.and(
                    JPAExpressions.selectOne()
                            .from(resumeTechStack)
                            .join(resumeTechStack.techStack, techStack)
                            .where(resumeTechStack.resume.eq(resume)
                                    .and(techStack.name.in(techStackNames)))
                            .exists()
            );
        }

        // Company 이름 필터
        List<String> companyNames = req.getCompanyNames();
        if (companyNames != null && !companyNames.isEmpty()) {
            builder.and(
                    JPAExpressions.selectOne()
                            .from(resumeCompany)
                            .join(resumeCompany.company, company)
                            .where(resumeCompany.resume.eq(resume)
                                    .and(company.name.in(companyNames)))
                            .exists()
            );
        }

        // 전체 개수 조회
        long total = queryFactory.select(resume.count())
                .from(resume)
                .where(builder)
                .fetchOne();

        // 페이징 결과 조회
        List<Resume> content = queryFactory.select(resume)
                .from(resume)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}