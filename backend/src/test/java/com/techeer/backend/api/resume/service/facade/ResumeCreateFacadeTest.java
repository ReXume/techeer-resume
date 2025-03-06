package com.techeer.backend.api.resume.service.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.domain.ResumePdf;
import com.techeer.backend.api.resume.dto.request.CreateResumeRequest;
import com.techeer.backend.api.resume.service.ResumePdfService;
import com.techeer.backend.api.resume.service.ResumeService;
import com.techeer.backend.api.tag.company.domain.Company;
import com.techeer.backend.api.tag.company.service.CompanyService;
import com.techeer.backend.api.tag.position.Position;
import com.techeer.backend.api.tag.techStack.domain.TechStack;
import com.techeer.backend.api.tag.techStack.service.TechStackService;
import com.techeer.backend.api.user.domain.User;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

/**
 * ResumeCreateFacade에 대한 단위 테스트 예시
 */
@ExtendWith(MockitoExtension.class)
class ResumeCreateFacadeTest {

    @InjectMocks
    private ResumeCreateFacade resumeCreateFacade;

    @Mock
    private ResumeService resumeService;

    @Mock
    private CompanyService companyService;

    @Mock
    private TechStackService techStackService;

    @Mock
    private ResumePdfService resumePdfService;

    @Captor
    private ArgumentCaptor<Resume> resumeCaptor;

    @BeforeEach
    void setUp() {
        // 혹은 @ExtendWith(MockitoExtension.class)로 충분
        // MockitoAnnotations.openMocks(this); // 중복 초기화라면 제거 가능
    }

    @Test
    @DisplayName("createResume() - 이전 이력서가 없는 경우, 정상적으로 이력서를 생성해야 한다")
    void createResume_WhenNoPreviousResume_ThenSuccess() {
        // Given
        User mockUser = User.builder()
                .username("testuser")
                .email("testuser@test.com")
                .build();

        // 임의의 TechStack, Company 리스트 Mock
        List<TechStack> mockTechStacks = List.of(new TechStack("Java"), new TechStack("Spring"));
        List<Company> mockCompanies = List.of(new Company("Google"), new Company("Naver"));
        given(techStackService.findOrCreateTechStacks(anyList())).willReturn(mockTechStacks);
        given(companyService.findOrCreateCompanies(anyList())).willReturn(mockCompanies);

        // 이전 이력서가 없는 경우
        given(resumeService.findLaterByUser(mockUser)).willReturn(null);

        // PDF 파일 Mock
        MockMultipartFile pdfFile = new MockMultipartFile(
                "resume_file",
                "test.pdf",
                "application/pdf",
                "dummy pdf content".getBytes()
        );

        // PDF 엔티티도 Mock 리턴
        ResumePdf mockResumePdf = mock(ResumePdf.class);
        given(resumePdfService.saveResumePdf(any(Resume.class), eq(pdfFile)))
                .willReturn(mockResumePdf);

        // Request DTO
        CreateResumeRequest request = CreateResumeRequest.builder()
                .position(Position.BACKEND)
                .career(5)
                .techStackNames(List.of("Java", "Spring"))
                .companyNames(List.of("Google", "Naver"))
                .build();

        // When
        resumeCreateFacade.createResume(mockUser, request, pdfFile);

        // Then
        // 1) resumeService.saveResume(...)가 제대로 호출되었는지 확인
        verify(resumeService, times(1)).saveResume(resumeCaptor.capture());
        Resume savedResume = resumeCaptor.getValue();

        // 2) savedResume의 필드가 올바른지 검증
        assertThat(savedResume.getUser()).isEqualTo(mockUser);
        assertThat(savedResume.getPosition()).isEqualTo(Position.BACKEND);
        assertThat(savedResume.getCareer()).isEqualTo(5);
        assertThat(savedResume.getName()).contains("Resume of testuser - " + LocalDate.now(ZoneId.of("Asia/Seoul")));
        // 이전 이력서가 없으므로 null이 맞다
        assertThat(savedResume.getPreviousResumeId()).isNull();

        // 3) PDF 업로드 로직이 호출되었는지 확인
        verify(resumePdfService).saveResumePdf(any(Resume.class), eq(pdfFile));

        // 4) user.addResume(...)가 잘 호출되었는지 여부
        //    실제 user 객체를 Spy로 만들지 않았다면,
        //    여기서는 user.addResume 호출 여부까지는 직접 검증하기 어렵다.
        //    Facade가 user.addResume(...)를 호출했음을 verify 하려면
        //    user 자체를 @SpyBean / @Spy로 선언해야 한다(혹은 Domain Event 검사).

        // (추가) TechStack / Company 관련 검증
        //  - 실제 DB가 없으므로, 호출 여부만 검증
        verify(techStackService, times(1)).findOrCreateTechStacks(request.getTechStackNames());
        verify(companyService, times(1)).findOrCreateCompanies(request.getCompanyNames());

        // (추가) 필요한 경우, ResumeTechStack / ResumeCompany가 resume에 잘 들어갔는지
        //       ArgumentCaptor나 getter를 통해 확인 가능
        assertThat(savedResume.getResumeTechStacks()).hasSize(mockTechStacks.size());
        assertThat(savedResume.getResumeCompanies()).hasSize(mockCompanies.size());
    }

    /**
     * 이전 이력서가 있는 경우 (previousResume != null) 시나리오:
     * - previousResume.updateLaterResumeId(...)가 호출되는지,
     * - 새로 생성된 resume에 previousResumeId가 제대로 들어가는지
     * 등을 별도의 테스트로 작성 가능
     */
}
