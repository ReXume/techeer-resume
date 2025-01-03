package com.techeer.backend.api.resume.service;

import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.dto.request.ResumeSearchRequest;
import com.techeer.backend.api.resume.exception.ResumeNotFoundException;
import com.techeer.backend.api.resume.repository.ResumeRepository;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumeService {
    private final ResumeRepository resumeRepository;

    public Resume saveResume(Resume resume) {
        Resume savedResume = resumeRepository.save(resume);
        return savedResume;
    }

    // 이력서 개별 조회
    public Resume getResume(Long resumeId) {
        return resumeRepository.findById(resumeId)
                .orElseThrow(ResumeNotFoundException::new);
    }

    // 유저 이름으로 이력서 조회
    public List<Resume> searchResumesByUserName(String userName) {
        return resumeRepository.findResumesByUsername(userName);
    }

    // 태그 조회
    public Page<Resume> searchByTages(ResumeSearchRequest req, Pageable pageable) {
        return resumeRepository.searchByCriteria(req, pageable);
    }


    public Page<Resume> getResumePage(Pageable pageable) {
        // 페이지네이션을 적용하여 레포지토리에서 데이터를 가져옴
        Page<Resume> resumes = resumeRepository.findAll(pageable);

        // 첫 번째 Resume 객체를 가져옴 (예시로 첫 번째 요소를 변환)
        Resume resume = resumes.getContent().isEmpty() ? null : resumes.getContent().get(0);

        // Resume가 없을 경우 빈 결과를 처리
        if (resume == null) {
            throw new BusinessException(ErrorCode.RESUME_NOT_FOUND);
        }

        return resumes;
    }

    public Resume findLaterByUser(User user) {
        return resumeRepository.findFirstByUserOrderByCreatedAtDesc(user);
    }

    public Slice<Resume> getResumesByUser(User user) {
        return resumeRepository.findResumeByUser(user);
    }
}
