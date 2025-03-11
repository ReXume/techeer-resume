package com.techeer.backend.api.resume.service;

import com.techeer.backend.api.user.domain.Role;
import com.techeer.backend.api.user.domain.SocialType;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.repository.UserRepository;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.jwt.service.JwtService;
import com.techeer.backend.global.redis.RedisService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


public class UserServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RedisService redisService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void logout_WhenUserExists_ShouldLogout() {
        // Arrange
        MockHttpServletResponse response = new MockHttpServletResponse();
        Cookie accessCookie = new Cookie("accessToken", "TestToken");
        Cookie refreshCookie = new Cookie("refreshToken", "TestToken");
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        User user = User.builder()
                .email("test@example.com")
                .username("test")
                .refreshToken("TestToken")
                .role(Role.REGULAR)
                .socialType(SocialType.GOOGLE)
                .build();

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        when(userService.getLoginUser()).thenReturn(user);
        doNothing().when(redisService).deleteCacheRefreshToken(user.getRefreshToken());

        // Act
        User result = userService.logout(response);

        // Assert
        // 쿠키가 삭제되었는지 확인
        Cookie[] cookies = response.getCookies();

        // ✅ "accessToken" 쿠키가 Max-Age=0으로 설정되었는지 확인 (삭제 여부 체크)
        assertTrue(Arrays.stream(cookies)
                .anyMatch(cookie -> cookie.getName().equals("accessToken") && cookie.getMaxAge() <= 0));

        // ✅ "refreshToken" 쿠키가 Max-Age=0으로 설정되었는지 확인 (삭제 여부 체크)
        assertTrue(Arrays.stream(cookies)
                .anyMatch(cookie -> cookie.getName().equals("refreshToken") && cookie.getMaxAge() <= 0));
    }
}
