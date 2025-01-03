package com.techeer.backend.api.resume.converter;

import com.techeer.backend.api.feedback.converter.FeedbackConverter;
import com.techeer.backend.api.feedback.domain.Feedback;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.dto.response.PageableResumeResponse;
import com.techeer.backend.api.resume.dto.response.ResumeDetailResponse;
import com.techeer.backend.api.resume.dto.response.ResumeResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResumeConverter {

    public static PageableResumeResponse toPageableResumeResponse(Resume resume) {
        return PageableResumeResponse.builder()
                .resumeId(resume.getId())
                .resumeName(resume.getName())
                .userName(resume.getUser().getUsername())
                .resumeName(resume.getName())
                .position(resume.getPosition().getValue())
                .career(resume.getCareer())
                .techStackNames(resume.getResumeTechStacks()
                        .stream()
                        .map(resumetechStack -> resumetechStack.getTechStack().getName()).collect(Collectors.toList()))
                .companyNames(resume.getResumeCompanies()
                        .stream()
                        .map(resumeCompany -> resumeCompany.getCompany().getName()).collect(Collectors.toList()))
                .build();
    }


    public static ResumeDetailResponse toResumeDetailResponse(Resume resume, List<Feedback> feedbacks) {
        return ResumeDetailResponse.builder()
                .resumeId(resume.getId())
                .resumeName(resume.getName())
                .userName(resume.getUser().getUsername())
                .position(resume.getPosition().getValue())
                .career(resume.getCareer())
                .techStackNames(resume.getResumeTechStacks()
                        .stream()
                        .map(resumetechStack -> resumetechStack.getTechStack().getName()).collect(Collectors.toList()))
                .companyNames(resume.getResumeCompanies()
                        .stream()
                        .map(resumeCompany -> resumeCompany.getCompany().getName()).collect(Collectors.toList()))
                .fileUrl(resume.getResumePdf().getPdf().getPdfUrl())
                .feedbackResponses(feedbacks.stream().map(FeedbackConverter::toFeedbackResponse).collect(Collectors.toList()))
                .previousResumeId(Optional.ofNullable(resume.getPreviousResumeId()).orElse(null))
                .laterResumeId(Optional.ofNullable(resume.getLaterResumeId()).orElse(null))
                .build();
    }


    public static ResumeResponse toResumeResponse(Resume resume) {
        return ResumeResponse.builder()
                .resumeId(resume.getId())
                .resumeName(resume.getName())
                .userName(resume.getUser().getUsername())
                .resumeName(resume.getName())
                .position(resume.getPosition().getValue())
                .career(resume.getCareer())
                .techStackNames(resume.getResumeTechStacks()
                        .stream()
                        .map(resumetechStack -> resumetechStack.getTechStack().getName()).collect(Collectors.toList()))
                .companyNames(resume.getResumeCompanies()
                        .stream()
                        .map(resumeCompany -> resumeCompany.getCompany().getName()).collect(Collectors.toList()))
                .build();

    }
}
