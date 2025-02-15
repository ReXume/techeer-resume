package com.techeer.backend.api.resume.controller;

import com.techeer.backend.api.feedback.domain.Feedback;
import com.techeer.backend.api.feedback.service.FeedbackService;
import com.techeer.backend.api.resume.converter.ResumeConverter;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.dto.request.CreateResumeRequest;
import com.techeer.backend.api.resume.dto.request.ResumeSearchRequest;
import com.techeer.backend.api.resume.dto.response.PageableResumeResponse;
import com.techeer.backend.api.resume.dto.response.ResumeDetailResponse;
import com.techeer.backend.api.resume.dto.response.ResumeResponse;
import com.techeer.backend.api.resume.service.ResumeService;
import com.techeer.backend.api.resume.service.facade.ResumeCreateFacade;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.common.response.CommonResponse;
import com.techeer.backend.global.error.ErrorCode;
import com.techeer.backend.global.error.exception.BusinessException;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//todo @RequiredArgsConstructor 추가 후 만든 생성자 삭제
@RestController
@RequiredArgsConstructor
@Tag(name = "resume", description = "Resume API")
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeCreateFacade resumeCreateFacade;
    private final ResumeService resumeService;
    private final FeedbackService feedbackService;
    private final UserService userService;

    // 이력서 등록
    @Operation(summary = "이력서 등록")
    @PostMapping(value = "/resumes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<?> resumeRegistration(@Valid @RequestPart("resume") CreateResumeRequest createResumeReq,
                                                @RequestPart(name = "resume_file")
                                                @Valid MultipartFile resumeFile) {
        // 파일 유효성 검사 -> 나중에 vaildtor로 변경해서 유효성 검사할 예정
        if (resumeFile.isEmpty()) {
            throw new BusinessException(ErrorCode.RESUME_FILE_EMPTY);
        }
        if (!resumeFile.getContentType().equals("application/pdf")) {
            throw new BusinessException(ErrorCode.RESUME_FILE_TYPE_NOT_ALLOWED);
        }
        // todo 유저 이름으로 객체 탐색
        //        Optional<User> registrars = userService.findUserByName(createResumeReq.getUsername());
        //        User registrar = null;
        //        if (registrars.isPresent()) {registrar = registrars.get();}

        resumeCreateFacade.createResume(createResumeReq, resumeFile);
        return CommonResponse.of(SuccessCode.RESUME_CREATED, null);
    }


    @Operation(summary = "회원 이름으로 이력서 조회")
    @GetMapping("/resumes/search")
    public CommonResponse<List<ResumeResponse>> searchResumesByUserName(@RequestParam("user_name") String userName) {
        User user = userService.getLoginUser();

        List<Resume> resumes = resumeService.searchResumesByUserName(userName);

        List<ResumeResponse> resumeResponse = resumes.stream()
                .map(ResumeConverter::toResumeResponse)
                .collect(Collectors.toList());

        return CommonResponse.of(SuccessCode.RESUME_FETCH_OK, resumeResponse);
    }

    @Operation(summary = "유저 이력서 조회")
    @GetMapping("/resumes/user")
    public CommonResponse<?> searchByUser() {
        User user = userService.getLoginUser();
        Slice<Resume> resumes = resumeService.getResumesByUser(user);
        List<ResumeResponse> resumeResponses = resumes.stream()
                .map(ResumeConverter::toResumeResponse)
                .collect(Collectors.toList());
        return CommonResponse.of(SuccessCode.OK, resumeResponses);
    }


    @Operation(summary = "이력서 개별 조회")
    @GetMapping("/resumes/{resume_id}")
    public CommonResponse<ResumeDetailResponse> searchResumeDetail(@PathVariable("resume_id") Long resumeId) {

        Resume resume = resumeService.getResume(resumeId);
        List<Feedback> feedbakcs = feedbackService.getFeedbacksByResumeId(resumeId);

        ResumeDetailResponse resumeContent = ResumeConverter.toResumeDetailResponse(resume, feedbakcs);
        return CommonResponse.of(SuccessCode.OK, resumeContent);
    }


    @Operation(summary = "여러 이력서 조회(페이지네이션)")
    @GetMapping(value = "/resumes")
    public CommonResponse<List<PageableResumeResponse>> searchResumes(@RequestParam(name = "page") int page,
                                                                      @RequestParam(name = "size") int size) {
        //ResumeService를 통해 페이지네이션된 이력서 목록을 가져옵니다.
        final Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<Resume> resumes = resumeService.getResumePage(pageable);

        List<PageableResumeResponse> resumeResponses = resumes.stream()
                .map(ResumeConverter::toPageableResumeResponse)
                .collect(Collectors.toList());

        return CommonResponse.of(SuccessCode.OK, resumeResponses);
    }


    @Operation(summary = "이력서 태그 조회")
    @PostMapping("/resumes/search")
    public CommonResponse<List<PageableResumeResponse>> searchResumesByTag(@RequestParam(name = "page") int page,
                                                                           @RequestParam(name = "size") int size,
                                                                           @RequestBody ResumeSearchRequest dto) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<Resume> resumeList = resumeService.searchByTages(dto, pageable);

        List<PageableResumeResponse> pageableResumeResponse = resumeList.stream()
                .map(ResumeConverter::toPageableResumeResponse)
                .collect(Collectors.toList());

        return CommonResponse.of(SuccessCode.OK, pageableResumeResponse);
    }

    @Operation(summary = "이력서 삭제")
    @DeleteMapping("/resumes/{resume_id}")
    public CommonResponse<?> deleteResume(@PathVariable("resume_id") Long resumeId) {
        User user = userService.getLoginUser();

        resumeService.softDeleteResume(user, resumeId);
        return CommonResponse.of(SuccessCode.RESUME_SOFT_DELETED, null);
    }
}
