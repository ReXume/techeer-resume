package com.techeer.backend.api.resume.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techeer.backend.api.bookmark.domain.Bookmark;
import com.techeer.backend.api.bookmark.repository.BookmarkRepository;
import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.resume.repository.ResumeRepository;
import com.techeer.backend.api.tag.position.Position;
import com.techeer.backend.api.user.domain.Role;
import com.techeer.backend.api.user.domain.SocialType;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.global.jwt.service.JwtService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EntityManager em;

    private User testUser;
    private Resume testResume;
    private String accessToken;

    @BeforeEach
    void setUp() {
        // Given: 테스트용 User와 Resume를 DB에 저장
        testUser = User.builder()
                .email("john@example.com")
                .username("JohnDoe")
                .role(Role.TECHEER)
                .socialType(SocialType.GOOGLE)
                .build();
        userRepository.save(testUser);

        testResume = Resume.builder()
                .user(testUser)
                .name("John's Resume")
                .career(5)
                .position(Position.DEVOPS)
                .build();
        resumeRepository.save(testResume);

        // EntityManager를 사용해 상태를 초기화
        em.flush();
        em.clear();

        // JWT 토큰 생성
        accessToken = jwtService.createAccessToken(testUser.getEmail());
    }

    @Test
    @DisplayName("Given 유효한 데이터, When 북마크 추가 요청, Then 201 상태코드 반환")
    void addBookmark_ValidData() throws Exception {
        // When: 북마크 추가 요청
        mockMvc.perform(post("/api/v1/bookmarks/{resume_id}", testResume.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken", accessToken)))// resume_id를 쿼리 파라미터로 전달
                // Then: 201 Created 응답
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.http_status").value("CREATED"))
                .andExpect(jsonPath("$.result.resume_id").value(testResume.getId()));

        // 북마크가 저장되었는지 검증
        List<Bookmark> bookmarks = bookmarkRepository.findAll();
        assertThat(bookmarks).hasSize(1);
        assertThat(bookmarks.get(0).getResume().getId()).isEqualTo(testResume.getId());
    }


    @Test
    @DisplayName("Given 북마크가 존재, When 북마크 삭제 요청, Then 204 상태코드 반환")
    void removeBookmark_ValidData() throws Exception {
        // Given: 북마크 저장
        Bookmark bookmark = Bookmark.builder()
                .user(testUser)
                .resume(testResume)
                .build();
        bookmarkRepository.save(bookmark);

        // When: 북마크 삭제 요청
        mockMvc.perform(delete("/api/v1/bookmarks/{bookmark_id}", bookmark.getId())
                        .cookie(new Cookie("accessToken", accessToken)))
                // Then: 204 No Content 응답
                .andExpect(status().isNoContent());

        // 북마크가 삭제되었는지 검증
        List<Bookmark> bookmarks = bookmarkRepository.findAll();
        assertThat(bookmarks).isEmpty();
    }

    @Test
    @DisplayName("Given 북마크가 없는 사용자, When 북마크 조회 요청, Then 빈 리스트와 200 상태코드 반환")
    void getBookmarksByUserId_NoBookmarks() throws Exception {
        // When: 북마크 조회 요청
        mockMvc.perform(get("/api/v1/bookmarks/users/{user_id}", testUser.getId())
                        .cookie(new Cookie("accessToken", accessToken)))
                // Then: 200 OK 응답과 빈 리스트 반환
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.http_status").value("OK"))
                .andExpect(jsonPath("$.result").isEmpty());
    }

    @Test
    @DisplayName("Given 북마크가 있는 사용자, When 북마크 조회 요청, Then 북마크 리스트와 200 상태코드 반환")
    void getBookmarksByUserId_WithBookmarks() throws Exception {
        // Given: 북마크 저장
        Bookmark bookmark = Bookmark.builder()
                .user(testUser)
                .resume(testResume)
                .build();
        bookmarkRepository.save(bookmark);

        // When: 북마크 조회 요청
        mockMvc.perform(get("/api/v1/bookmarks/users/{user_id}", testUser.getId())
                        .cookie(new Cookie("accessToken", accessToken)))
                // Then: 200 OK 응답과 북마크 리스트 반환
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.http_status").value("OK"))
                .andExpect(jsonPath("$.result").isNotEmpty())
                .andExpect(jsonPath("$.result[0].resume_id").value(testResume.getId()));
    }
}
