package com.techeer.backend.infra.gcp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
public class GcsConfig {

	@Value("${gcp.project-id}")
	private String projectId;

	@Value("${gcp.service-account.key-path:}")
	private String keyPath;

	@Value("${gcp.service-account.key-json:}")
	private String keyJson;

	@Bean
	public Storage storage() throws IOException {
		GoogleCredentials credentials;

		// key-path를 우선 체크 (사용자가 주로 사용하는 방식)
		if (StringUtils.hasText(keyPath)) {
			log.info("GCP 인증: 파일 경로 사용 - {}", keyPath);
			InputStream keyStream = null;

			try {
				if (keyPath.startsWith("classpath:")) {
					// classpath 리소스로 읽기 (resources 폴더에 파일이 있을 때)
					// 예: classpath:gcp-service-account-key.json
					String resourcePath = keyPath.substring("classpath:".length());
					keyStream = new ClassPathResource(resourcePath).getInputStream();
					log.info("GCP 인증: classpath 리소스 사용 - {}", resourcePath);
				}
				else {
					// 절대 경로 또는 상대 경로로 읽기
					File keyFile = new File(keyPath);

					// 상대 경로인 경우 프로젝트 루트 기준으로 처리
					if (!keyFile.isAbsolute() && !keyFile.exists()) {
						// 프로젝트 루트 기준으로 다시 시도
						String projectRoot = System.getProperty("user.dir");
						keyFile = new File(projectRoot, keyPath);
						log.info("GCP 인증: 상대 경로를 프로젝트 루트 기준으로 변환 - {} -> {}", keyPath, keyFile.getAbsolutePath());
					}

					if (!keyFile.exists()) {
						throw new IllegalStateException(String.format("GCP 인증 파일을 찾을 수 없습니다: %s (절대 경로: %s)", keyPath,
								keyFile.getAbsolutePath()));
					}

					keyStream = new FileInputStream(keyFile);
					log.info("GCP 인증: 파일 시스템 경로 사용 - {}", keyFile.getAbsolutePath());
				}

				credentials = GoogleCredentials.fromStream(keyStream);
			}
			finally {
				if (keyStream != null) {
					keyStream.close();
				}
			}
		}
		// key-json이 있으면 사용 (Docker 환경 등에서 환경변수로 직접 전달할 때)
		else if (StringUtils.hasText(keyJson)) {
			log.info("GCP 인증: 환경변수 JSON 사용");
			credentials = GoogleCredentials
				.fromStream(new ByteArrayInputStream(keyJson.getBytes(StandardCharsets.UTF_8)));
		}
		else {
			throw new IllegalStateException("GCP 인증 정보가 설정되지 않았습니다. "
					+ "gcp.service-account.key-path 또는 gcp.service-account.key-json을 설정해주세요.");
		}

		return StorageOptions.newBuilder().setProjectId(projectId).setCredentials(credentials).build().getService();
	}

}
