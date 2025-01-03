package com.techeer.backend.api.resume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.exception.ResumeNotFoundException;
import com.techeer.backend.api.resume.repository.ResumeRepository;
import com.techeer.backend.api.tag.position.Position;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.api.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ResumeServiceIntegrationTest {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        // 사용자 초기화
        user = User.builder()
                .username("john_doe")
                .build();
        userRepository.save(user);
    }

    @Test
    void getResume_WhenResumeExists_ShouldReturnResume() {
        // Arrange
        Resume resume = Resume.builder()
                .user(user)
                .name("John's Resume")
                .career(5)
                .position(Position.DEVOPS)
                .build();
        resumeRepository.save(resume);

        // Act
        Resume foundResume = resumeService.getResume(resume.getId());

        // Assert
        assertNotNull(foundResume);
        assertEquals("John's Resume", foundResume.getName());
    }

    @Test
    void getResume_WhenResumeDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        Long invalidResumeId = 999L;

        // Act & Assert
        assertThrows(ResumeNotFoundException.class, () -> resumeService.getResume(invalidResumeId));
    }

    @Test
    void searchResumesByUserName_ShouldReturnResumes() {
        // Arrange
        Resume resume1 = Resume.builder()
                .user(user)
                .name("John's Resume 1")
                .career(3)
                .position(Position.BACKEND)
                .build();

        Resume resume2 = Resume.builder()
                .user(user)
                .name("John's Resume 2")
                .career(5)
                .position(Position.BACKEND)
                .build();

        resumeRepository.save(resume1);
        resumeRepository.save(resume2);

        // Act
        var resumes = resumeService.searchResumesByUserName("john_doe");

        // Assert
        assertEquals(2, resumes.size());
    }
}
