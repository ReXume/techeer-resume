package com.techeer.backend.api.resume.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techeer.backend.api.feedback.service.FeedbackService;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.service.ResumeService;
import com.techeer.backend.api.resume.service.facade.ResumeCreateFacade;
import com.techeer.backend.api.tag.position.Position;
import com.techeer.backend.api.user.domain.Role;
import com.techeer.backend.api.user.domain.SocialType;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.service.UserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ResumeController.class)
class ResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResumeCreateFacade resumeCreateFacade;

    @MockBean
    private ResumeService resumeService;

    @MockBean
    private UserService userService;

    @MockBean
    private FeedbackService feedbackService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(username = "john_doe", roles = {"USER"})
    void searchResumesByUserName_ShouldReturnListOfResumes() throws Exception {
        // Arrange
        String userName = "john_doe";

        // User 객체 생성
        User user = User.builder()
                .email("john@example.com")
                .username(userName)
                .refreshToken(null)
                .role(Role.TECHEER) // Role enum을 사용한다면 적절히 설정
                .socialType(SocialType.GOOGLE) // SocialType enum을 사용한다면 적절히 설정
                .build();

        // Resume 객체 생성
        Resume resume1 = Resume.builder()
                .id(1L)
                .name("John's Resume 1")
                .user(user)
                .position(Position.BACKEND)
                .career(5)
                .resumeTechStacks(new ArrayList<>())
                .resumeCompanies(new ArrayList<>())
                .resumePdf(null) // 필요 시 ResumePdf 객체 생성
                .build();

        Resume resume2 = Resume.builder()
                .id(2L)
                .name("John's Resume 2")
                .user(user)
                .position(Position.BACKEND)
                .career(5)
                .resumeTechStacks(new ArrayList<>())
                .resumeCompanies(new ArrayList<>())
                .resumePdf(null) // 필요 시 ResumePdf 객체 생성
                .build();

        List<Resume> mockedResumes = Arrays.asList(resume1, resume2);
        when(resumeService.searchResumesByUserName(userName)).thenReturn(mockedResumes);

        // Act & Assert
        mockMvc.perform(get("/api/v1/resumes/search")
                        .param("user_name", userName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].resumeId").value(1))
                .andExpect(jsonPath("$.result[0].resumeName").value("John's Resume 1"))
                .andExpect(jsonPath("$.result[1].resumeId").value(2))
                .andExpect(jsonPath("$.result[1].resumeName").value("John's Resume 2"));

        verify(resumeService, times(1)).searchResumesByUserName(userName);
    }
}
