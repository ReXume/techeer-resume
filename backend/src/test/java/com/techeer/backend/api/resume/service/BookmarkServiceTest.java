//package com.techeer.backend.api.resume.service;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import com.techeer.backend.api.bookmark.domain.Bookmark;
//import com.techeer.backend.api.bookmark.repository.BookmarkRepository;
//import com.techeer.backend.api.bookmark.service.BookmarkService;
//import com.techeer.backend.api.resume.domain.Resume;
//import com.techeer.backend.api.resume.repository.ResumeRepository;
//import com.techeer.backend.api.tag.position.Position;
//import com.techeer.backend.api.user.domain.Role;
//import com.techeer.backend.api.user.domain.SocialType;
//import com.techeer.backend.api.user.domain.User;
//import com.techeer.backend.global.error.ErrorCode;
//import com.techeer.backend.global.error.exception.BusinessException;
//import java.lang.reflect.Field;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class) // Spring 컨텍스트 로딩 없이 Mockito 확장 사용
//class BookmarkServiceTest {
//
//    @Mock
//    private BookmarkRepository bookmarkRepository;
//
//    @Mock
//    private ResumeRepository resumeRepository;
//
//    @InjectMocks
//    private BookmarkService bookmarkService;
//
//    private User user;
//    private Resume resume;
//    private Bookmark bookmark;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        // User 객체 생성
//        user = User.builder()
//                .email("testuser@example.com")
//                .username("testuser")
//                .refreshToken("refreshToken123")
//                .role(Role.TECHEER)
//                .socialType(SocialType.GOOGLE)
//                .build();
//
//        // 리플렉션을 사용하여 User의 id 설정
//        setField(user, "id", 1L);
//
//        // Resume 객체 생성
//        resume = Resume.builder()
//                .user(user)
//                .name("Sample Resume")
//                .career(5)
//                .position(Position.BACKEND)
//                .build();
//
//        // 리플렉션을 사용하여 Resume의 id 설정
//        setField(resume, "id", 1L);
//
//        // Bookmark 객체 생성
//        bookmark = Bookmark.builder()
//                .resume(resume)
//                .user(user)
//                .build();
//
//        // 리플렉션을 사용하여 Bookmark의 id 설정
//        setField(bookmark, "id", 1L);
//    }
//
//    /**
//     * 리플렉션을 사용하여 private 필드에 값을 설정하는 유틸리티 메서드
//     */
//    private void setField(Object target, String fieldName, Object value) throws Exception {
//        Field field = null;
//        Class<?> clazz = target.getClass();
//        while (clazz != null) {
//            try {
//                field = clazz.getDeclaredField(fieldName);
//                break;
//            } catch (NoSuchFieldException e) {
//                clazz = clazz.getSuperclass();
//            }
//        }
//        if (field == null) {
//            throw new NoSuchFieldException("Field '" + fieldName + "' not found on " + target.getClass());
//        }
//        field.setAccessible(true);
//        field.set(target, value);
//    }
//
//    @Nested
//    @DisplayName("addBookmark() Tests")
//    class AddBookmarkTests {
//
//        @Test
//        @DisplayName("Should add a bookmark successfully when resume exists")
//        void addBookmark_Success() {
//            // Given
//            Long resumeId = resume.getId();
//            when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(resume));
//            when(bookmarkRepository.save(any(Bookmark.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//            // When
//            Bookmark savedBookmark = bookmarkService.addBookmark(user, resumeId);
//
//            // Then
//            assertThat(savedBookmark).isNotNull();
//            assertThat(savedBookmark.getUser()).isEqualTo(user);
//            assertThat(savedBookmark.getResume()).isEqualTo(resume);
//            verify(resumeRepository, times(1)).findById(resumeId);
//            verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
//        }
//
//        @Test
//        @DisplayName("Should throw BusinessException when resume does not exist")
//        void addBookmark_ResumeNotFound() {
//            // Given
//            Long resumeId = 99L;
//            when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());
//
//            // When & Then
//            assertThatThrownBy(() -> bookmarkService.addBookmark(user, resumeId))
//                    .isInstanceOf(BusinessException.class)
//                    .extracting("errorCode")
//                    .isEqualTo(ErrorCode.RESUME_NOT_FOUND);
//
//            verify(resumeRepository, times(1)).findById(resumeId);
//            verify(bookmarkRepository, never()).save(any(Bookmark.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("removeBookmark() Tests")
//    class RemoveBookmarkTests {
//
//        @Test
//        @DisplayName("Should remove bookmark successfully when user is authorized")
//        void removeBookmark_Success() {
//            // Given
//            Long bookmarkId = bookmark.getId();
//            when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.of(bookmark));
//
//            // When
//            bookmarkService.removeBookmark(user, bookmarkId);
//
//            // Then
//            verify(bookmarkRepository, times(1)).findById(bookmarkId);
//            verify(bookmarkRepository, times(1)).delete(bookmark);
//        }
//
//        @Test
//        @DisplayName("Should throw BusinessException when bookmark does not exist")
//        void removeBookmark_BookmarkNotFound() {
//            // Given
//            Long bookmarkId = 99L;
//            when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.empty());
//
//            // When & Then
//            assertThatThrownBy(() -> bookmarkService.removeBookmark(user, bookmarkId))
//                    .isInstanceOf(BusinessException.class)
//                    .extracting("errorCode")
//                    .isEqualTo(ErrorCode.BOOKMARK_NOT_FOUND);
//
//            verify(bookmarkRepository, times(1)).findById(bookmarkId);
//            verify(bookmarkRepository, never()).delete(any(Bookmark.class));
//        }
//
//        @Test
//        @DisplayName("Should throw BusinessException when user is unauthorized")
//        void removeBookmark_Unauthorized() throws Exception {
//            // Given
//            Long bookmarkId = bookmark.getId();
//            User anotherUser = User.builder()
//                    .email("another@example.com")
//                    .username("anotherUser")
//                    .refreshToken("anotherRefreshToken")
//                    .role(Role.TECHEER)
//                    .socialType(SocialType.GITHUB)
//                    .build();
//            setField(anotherUser, "id", 2L);
//
//            Resume unauthorizedResume = Resume.builder()
//                    .user(anotherUser)
//                    .name("Unauthorized Resume")
//                    .career(3)
//                    .position(Position.DESIGNER)
//                    .build();
//            setField(unauthorizedResume, "id", 2L);
//
//            Bookmark unauthorizedBookmark = Bookmark.builder()
//                    .resume(unauthorizedResume)
//                    .user(anotherUser)
//                    .build();
//            setField(unauthorizedBookmark, "id", 2L);
//
//            when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.of(unauthorizedBookmark));
//
//            // When & Then
//            assertThatThrownBy(() -> bookmarkService.removeBookmark(user, bookmarkId))
//                    .isInstanceOf(BusinessException.class)
//                    .extracting("errorCode")
//                    .isEqualTo(ErrorCode.UNAUTHORIZED);
//
//            verify(bookmarkRepository, times(1)).findById(bookmarkId);
//            verify(bookmarkRepository, never()).delete(any(Bookmark.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("getBookmarksByUserId() Tests")
//    class GetBookmarksByUserIdTests {
//
//        @Test
//        @DisplayName("Should return list of bookmarks for a valid user")
//        void getBookmarksByUserId_Success() throws Exception {
//            // Given
//            Long userId = user.getId();
//            Bookmark bookmark1 = Bookmark.builder()
//                    .resume(resume)
//                    .user(user)
//                    .build();
//            setField(bookmark1, "id", 1L);
//
//            Bookmark bookmark2 = Bookmark.builder()
//                    .resume(resume)
//                    .user(user)
//                    .build();
//            setField(bookmark2, "id", 2L);
//
//            List<Bookmark> bookmarks = Arrays.asList(bookmark1, bookmark2);
//            when(bookmarkRepository.findAllByUserId(userId)).thenReturn(bookmarks);
//
//            // When
//            List<Bookmark> result = bookmarkService.getBookmarksByUserId(userId);
//
//            // Then
//            assertThat(result).isNotNull().hasSize(2).containsExactly(bookmark1, bookmark2);
//            verify(bookmarkRepository, times(1)).findAllByUserId(userId);
//        }
//
//        @Test
//        @DisplayName("Should return empty list when user has no bookmarks")
//        void getBookmarksByUserId_Empty() {
//            // Given
//            Long userId = user.getId();
//            when(bookmarkRepository.findAllByUserId(userId)).thenReturn(List.of());
//
//            // When
//            List<Bookmark> result = bookmarkService.getBookmarksByUserId(userId);
//
//            // Then
//            assertThat(result).isNotNull().isEmpty();
//            verify(bookmarkRepository, times(1)).findAllByUserId(userId);
//        }
//    }
//}
