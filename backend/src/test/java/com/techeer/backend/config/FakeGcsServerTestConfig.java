package com.techeer.backend.config;

import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.time.Duration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class FakeGcsServerTestConfig {

	private static final int FAKE_GCS_PORT = 4443;

	private static final String FAKE_GCS_IMAGE = "fsouza/fake-gcs-server:1.47.7";

	@Bean(initMethod = "start", destroyMethod = "stop")
	public GenericContainer<?> fakeGcsServer() {
		System.out.println("🚀 Starting Fake GCS Server container...");

		GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse(FAKE_GCS_IMAGE))
			.withExposedPorts(FAKE_GCS_PORT)
			.withCommand("-scheme", "http", "-port", String.valueOf(FAKE_GCS_PORT))
			.waitingFor(new HttpWaitStrategy().forPath("/storage/v1/b")
				.forPort(FAKE_GCS_PORT)
				.withStartupTimeout(Duration.ofSeconds(60)));

		return container;
	}

	@Bean
	@Primary
	public Storage storage(GenericContainer<?> fakeGcsServer) {
		String host = fakeGcsServer.getHost();
		Integer port = fakeGcsServer.getMappedPort(FAKE_GCS_PORT);
		String endpoint = String.format("http://%s:%d", host, port);

		System.out.println("📦 Configuring GCS Storage with endpoint: " + endpoint);

		// 타임아웃과 재시도 설정
		RetrySettings retrySettings = RetrySettings.newBuilder()
			.setMaxAttempts(3)
			.setTotalTimeout(org.threeten.bp.Duration.ofSeconds(10))
			.setInitialRetryDelay(org.threeten.bp.Duration.ofMillis(100))
			.setMaxRetryDelay(org.threeten.bp.Duration.ofSeconds(1))
			.build();

		return StorageOptions.newBuilder()
			.setHost(endpoint)
			.setProjectId("test-project")
			.setRetrySettings(retrySettings)
			.build()
			.getService();
	}

}
