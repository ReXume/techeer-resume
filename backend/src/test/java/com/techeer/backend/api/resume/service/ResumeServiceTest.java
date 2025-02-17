// src/test/java/com/techeer/backend/api/resume/service/ResumeServiceTest.java

package com.techeer.backend.api.resume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.exception.ResumeNotFoundException;
import com.techeer.backend.api.resume.repository.ResumeRepository;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.util.domain.ResumeUtils;
import com.techeer.backend.util.domain.UserUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ResumeServiceTest {

    @InjectMocks
    private ResumeService resumeService;

    @Mock
    private ResumeRepository resumeRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getResume_WhenResumeExists_ShouldReturnResume() {
        // Arrange
        Long resumeId = 1L;
        Resume resume = mock(Resume.class);
        when(resume.getId()).thenReturn(resumeId);
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));

        // Act
        Resume foundResume = resumeService.getResume(resumeId);

        // Assert
        assertNotNull(foundResume);
        assertEquals(resumeId, foundResume.getId());
        verify(resumeRepository, times(1)).findById(resumeId);
    }

    @Test
    void getResume_WhenResumeDoesNotExist_ShouldThrowNotFoundException() {
        // Arrange
        Long resumeId = 1L;
        when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResumeNotFoundException.class, () -> resumeService.getResume(resumeId));
        verify(resumeRepository, times(1)).findById(resumeId);
    }

    @Test
    void searchResumesByUserName_ShouldReturnListOfResumes() {
        // Arrange
        String username = "john_doe";
        Resume resume1 = Resume.builder().build();
        Resume resume2 = Resume.builder().build();
        List<Resume> resumes = Arrays.asList(resume1, resume2);
        when(resumeRepository.findResumesByUsername(username)).thenReturn(resumes);

        // Act
        List<Resume> result = resumeService.searchResumesByUserName(username);

        // Assert
        assertEquals(2, result.size());
        verify(resumeRepository, times(1)).findResumesByUsername(username);
    }

    @Test
    void searchResumesByUserName_ShouldReturnListOfResumes1() {
        // Arrange
        String userName = "john_doe";

        Resume resume1 = mock(Resume.class);
        when(resume1.getId()).thenReturn(1L);
        when(resume1.getName()).thenReturn("John's Resume 1");
        when(resume1.getUser()).thenReturn(mock(com.techeer.backend.api.user.domain.User.class));
        when(resume1.getUser().getUsername()).thenReturn(userName);
        when(resume1.getPosition()).thenReturn(com.techeer.backend.api.tag.position.Position.BACKEND);
        when(resume1.getCareer()).thenReturn(5);
        when(resume1.getResumeTechStacks()).thenReturn(Arrays.asList());
        when(resume1.getResumeCompanies()).thenReturn(Arrays.asList());

        Resume resume2 = mock(Resume.class);
        when(resume2.getId()).thenReturn(2L);
        when(resume2.getName()).thenReturn("John's Resume 2");
        when(resume2.getUser()).thenReturn(mock(com.techeer.backend.api.user.domain.User.class));
        when(resume2.getUser().getUsername()).thenReturn(userName);
        when(resume2.getPosition()).thenReturn(com.techeer.backend.api.tag.position.Position.BACKEND);
        when(resume2.getCareer()).thenReturn(3);
        when(resume2.getResumeTechStacks()).thenReturn(Arrays.asList());
        when(resume2.getResumeCompanies()).thenReturn(Arrays.asList());

        List<Resume> mockedResumes = Arrays.asList(resume1, resume2);
        when(resumeRepository.findResumesByUsername(userName)).thenReturn(mockedResumes);

        // Act
        List<Resume> result = resumeService.searchResumesByUserName(userName);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("John's Resume 1", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("John's Resume 2", result.get(1).getName());

        verify(resumeRepository, times(1)).findResumesByUsername(userName);
    }

    @Test
    void deleteResume_WhenResumeExists_ShouldDeleteResume() {
        // Given (테스트 준비)
        User user = UserUtils.newInstance();
        Resume resume = ResumeUtils.newInstance();
        Long resumeId = 1L;

        // Mocking: 특정 user와 resumeId로 조회하면 해당 resume 반환
        when(resumeRepository.findByUserAndId(user, resumeId)).thenReturn(Optional.of(resume));

        // When (테스트 실행)
        resumeService.softDeleteResume(user, resumeId);

        // Then (결과 검증)
        assertNotNull(resume.getDeletedAt()); // deletedAt이 null이 아니면 softDelete()가 정상 동작한 것
        verify(resumeRepository, times(1)).findByUserAndId(user, resumeId);
    }

}
