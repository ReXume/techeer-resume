package com.techeer.backend.api.resume.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.dto.request.CreateResumeRequest;
import com.techeer.backend.api.resume.repository.ResumeRepository;
import com.techeer.backend.api.resume.service.facade.ResumeCreateFacade;
import com.techeer.backend.api.tag.position.Position;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.infra.aws.S3Uploader;
import com.techeer.backend.util.domain.UserUtils;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ResumeIntegrationtest {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeCreateFacade resumeCreateFacade;

    @MockBean
    private S3Uploader s3Uploader;

    // ★ UserService를 MockBean으로 등록 (스프링 컨테이너에 Mock 객체로 주입)
    @MockBean
    private UserService userService;


    /**
     * [테스트 2] 동시성 테스트 - 20개의 스레드가 동시에 이력서 생성 - MockMvc 대신 Facade를 직접 호출하여 동시성 이슈가 있는지 확인
     */
    @Test
    @DisplayName("한 번에 20개의 이력서 생성 요청을 넣어도 동시성 문제가 없어야 한다.")
    void concurrentResumeCreationTest() throws InterruptedException {
        // given
        int threadCount = 20;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // 테스트용 User 생성
        User testUser = userRepository.saveAndFlush(UserUtils.newInstance());
//        when(userService.getLoginUser()).thenReturn(testUser);

        // Request DTO
        CreateResumeRequest request = CreateResumeRequest.builder()
                .position(Position.BACKEND)
                .career(5)
                .techStackNames(List.of("Java", "Spring"))
                .companyNames(List.of("Google", "Naver"))
                .build();

        // 4) 업로드할 PDF 파일(파트 이름 "resume_file")
        MockMultipartFile resumeFile = new MockMultipartFile(
                "resume_file",
                "resume.pdf",
                "application/pdf",
                new byte[1024] // 1KB짜리 PDF
        );

        // 5) S3 업로더 Mock 설정
        given(s3Uploader.uploadPdf(any(MultipartFile.class)))
                .willReturn("https://s3.bucket.com/resume.pdf");

        // when: 20개의 쓰레드가 동시에 createResume() 호출
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 여기서 userService.getLoginUser() 대신,
                    // 예제에서는 testUser를 반환하도록 getLoginUser()를 Mocking할 수 있음
                    // 혹은 코드상 userService.getLoginUser()가 testUser를 반환한다고 가정
                    resumeCreateFacade.createResume(testUser, request, resumeFile);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        // 1) Resume가 20개 생성되었는지
        long resumeCount = resumeRepository.count();
        assertThat(resumeCount).isEqualTo(threadCount);

        // 2) testUser를 다시 조회하여 연결된 Resumes 개수 확인
//        User foundUser = userRepository.findByEmail("testuser@test.com").orElseThrow();
        assertThat(testUser.getResumes()).hasSize(threadCount);

        // 3) previousResumeId / laterResumeId가 최소한 정상적으로 연결되었는지
        //    (동시에 생성하므로, 정확히 "1->2->3->..."가 아닐 수도 있으나,
        //    적어도 누락되거나 중복되지 않는지 정도는 확인 가능)
        List<Resume> allResumes = resumeRepository.findAll();

        // 예: previousResumeId가 null인 Resume는 최소 1개 이상
        long nullPrevCount = allResumes.stream()
                .filter(r -> r.getPreviousResumeId() == null)
                .count();
        assertThat(nullPrevCount).isGreaterThan(0);

        // 예: laterResumeId가 null인 Resume도 최소 1개 이상
        long nullLaterCount = allResumes.stream()
                .filter(r -> r.getLaterResumeId() == null)
                .count();
        assertThat(nullLaterCount).isGreaterThan(0);
    }
}
