package com.techeer.backend.integration.user;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techeer.backend.api.user.dto.request.LoginRequest;
import com.techeer.backend.api.user.dto.request.RegisterRequest;
import com.techeer.backend.config.FakeGcsServerTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
@Testcontainers
@Import(FakeGcsServerTestConfig.class)
@org.springframework.test.context.ActiveProfiles("test")
class UserApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("회원가입 API 테스트 - 성공 시 사용자가 생성되고 201 응답을 반환한다")
	void register() throws Exception {
		// Given
		RegisterRequest request = new RegisterRequest("integration@example.com", "IntegrationUser", "password1234");

		// When & Then
		mockMvc
			.perform(post("/api/v1/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").exists())
			.andDo(document("auth/register",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
							fieldWithPath("email").description("사용자 이메일 (유효한 이메일 형식)"),
							fieldWithPath("username").description("사용자 이름"),
							fieldWithPath("password").description("비밀번호 (최소 8자, 영문+숫자 포함)")
					),
					responseFields(
							fieldWithPath("success").description("성공 여부"),
							fieldWithPath("message").description("응답 메시지"),
							fieldWithPath("timestamp").description("응답 시간")
					)));
	}

	@Test
	@DisplayName("로그인 API 테스트 - 유효한 사용자 정보로 로그인 시 200 응답과 토큰을 반환한다")
	void login() throws Exception {
		// Given: 먼저 사용자를 생성
		RegisterRequest registerRequest = new RegisterRequest("login@example.com", "LoginUser", "password1234");
		mockMvc.perform(post("/api/v1/auth/register")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(registerRequest)));

		LoginRequest request = new LoginRequest("login@example.com", "password1234");

		// When & Then
		mockMvc
			.perform(post("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").exists())
			.andDo(document("auth/login",
					preprocessRequest(prettyPrint()),
					preprocessResponse(prettyPrint()),
					requestFields(
							fieldWithPath("email").description("사용자 이메일"),
							fieldWithPath("password").description("비밀번호")
					),
					responseFields(
							fieldWithPath("success").description("성공 여부"),
							fieldWithPath("message").description("응답 메시지"),
							fieldWithPath("timestamp").description("응답 시간")
					)));
	}

}
