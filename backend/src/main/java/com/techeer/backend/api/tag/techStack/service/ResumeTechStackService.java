package com.techeer.backend.api.tag.techStack.service;

import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.tag.techStack.domain.ResumeTechStack;
import com.techeer.backend.api.tag.techStack.domain.TechStack;
import com.techeer.backend.api.tag.techStack.repository.ResumeTechStackRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class ResumeTechStackService {

    private final ResumeTechStackRepository resumeTechStackRepository;


    public ResumeTechStack saveResumeTechStack(Resume resume, TechStack techStack) {
        return resumeTechStackRepository.save(
                ResumeTechStack.builder()
                        .resume(resume)
                        .techStack(techStack)
                        .build()
        );
    }

}
