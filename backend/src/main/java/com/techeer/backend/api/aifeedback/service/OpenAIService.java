package com.techeer.backend.api.aifeedback.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenAIService {

	@Value("${chatgpt.api.key}")
	private String apiKey;

	@Value("${chatgpt.api.url}")
	private String apiUrl;

	private final CloseableHttpClient httpClient;
	private static final int TIMEOUT = 30000;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public OpenAIService() {
		RequestConfig requestConfig = RequestConfig.custom()
			.setConnectTimeout(TIMEOUT)
			.setSocketTimeout(TIMEOUT)
			.build();

		this.httpClient = HttpClients.custom()
			.setDefaultRequestConfig(requestConfig)
			.build();
	}

	public String getAIFeedback(String resumeText) throws IOException {
		HttpPost httpPost = new HttpPost(apiUrl);
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("Authorization", "Bearer " + apiKey);

		// JSON 요청 본문 생성
		StringEntity entity = new StringEntity(createRequestBody(resumeText));
		httpPost.setEntity(entity);

		HttpResponse response = httpClient.execute(httpPost);
		int statusCode = response.getStatusLine().getStatusCode();

		if (statusCode != 200) {
			String errorMessage = EntityUtils.toString(response.getEntity());
			throw new IOException("OpenAI API 호출 실패: 상태 코드 " + statusCode + ", 응답: " + errorMessage);
		}

		return EntityUtils.toString(response.getEntity());
	}

	// ObjectMapper를 사용하여 JSON 요청 본문 생성
	private String createRequestBody(String resumeText) throws IOException {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("model", "gpt-4o");

		Map<String, String> message = new HashMap<>();
		message.put("role", "user");
		message.put("content", "\"이 이력서에서 잘 작성된 부분과 개선해야 할 부분을 구체적으로 지적해 주세요. 특히, 내용의 명확성, 경험 기술의 구체성, 그리고 부족한 스킬이나 프로젝트가 있는지에 대한 피드백을 제공해 주세요.\": \n" + resumeText);

		requestBody.put("messages", new Object[] {message});

		return objectMapper.writeValueAsString(requestBody);
	}
}
