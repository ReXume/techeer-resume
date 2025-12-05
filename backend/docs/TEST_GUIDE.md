# Spring Boot 통합 테스트 가이드 (Testcontainers & Fake GCS Server)

이 문서는 백엔드 프로젝트의 통합 테스트 환경 설정과 **Testcontainers**, **Fake GCS Server**를 활용한 테스트 방법에 대해 설명합니다.

## 1. 통합 테스트 환경 개요

우리는 실제 운영 환경과 최대한 유사한 환경에서 테스트를 수행하기 위해 **Testcontainers**를 사용합니다. 특히 Google Cloud Storage(GCS)와 같은 외부 서비스를 테스트할 때, Mock 객체를 사용하는 대신 실제 동작하는 에뮬레이터를 컨테이너로 띄워 테스트합니다.

### 주요 기술
- **Testcontainers**: JUnit 테스트 내에서 Docker 컨테이너를 관리(실행/종료)해주는 라이브러리
- **Fake GCS Server**: Google Cloud Storage API를 에뮬레이션하는 Go 기반의 가벼운 서버 (`fsouza/fake-gcs-server`)

## 2. 설정 방법

### 2.1 의존성 추가 (build.gradle)

```groovy
// Testcontainers
testImplementation 'org.testcontainers:testcontainers:1.19.3'
testImplementation 'org.testcontainers:junit-jupiter:1.19.3'
testImplementation 'org.testcontainers:gcloud:1.19.3'
```

### 2.2 Fake GCS Server 설정 (FakeGcsServerTestConfig.java)

테스트 실행 시 Docker 컨테이너로 Fake GCS Server를 띄우고, Spring의 `Storage` Bean이 이 가짜 서버를 바라보도록 재정의합니다.

```java
@TestConfiguration
public class FakeGcsServerTestConfig {

    private static final int FAKE_GCS_PORT = 4443;
    private static final String FAKE_GCS_IMAGE = "fsouza/fake-gcs-server:1.47.7";

    @Bean(initMethod = "start", destroyMethod = "stop")
    public GenericContainer<?> fakeGcsServer() {
        return new GenericContainer<>(DockerImageName.parse(FAKE_GCS_IMAGE))
            .withExposedPorts(FAKE_GCS_PORT)
            .withCommand("-scheme", "http", "-port", String.valueOf(FAKE_GCS_PORT))
            .waitingFor(new HttpWaitStrategy()
                .forPath("/storage/v1/b")
                .forPort(FAKE_GCS_PORT)
                .withStartupTimeout(Duration.ofSeconds(60)));
    }

    @Bean
    @Primary
    public Storage storage(GenericContainer<?> fakeGcsServer) {
        // Fake GCS Server의 호스트와 포트를 가져와서 StorageOptions 설정
        String endpoint = String.format("http://%s:%d", fakeGcsServer.getHost(), fakeGcsServer.getMappedPort(FAKE_GCS_PORT));
        
        return StorageOptions.newBuilder()
            .setHost(endpoint)
            .setProjectId("test-project")
            .build()
            .getService();
    }
}
```

### 2.3 테스트 환경 설정 (application.yml)

테스트 프로파일에서는 GCS 설정을 테스트용 값으로 오버라이딩해야 합니다. 실제 GCS에 연결되지 않도록 주의하세요.

```yaml
# src/test/resources/application.yml
gcp:
  project-id: test-project
  gcs:
    bucket: test-bucket # Fake GCS Server 내에서 사용할 버킷 이름
    region: us-central1
    folders:
      profile: profile
      document: document
      verification: verification
```

## 3. 통합 테스트 작성 예시

컨트롤러나 서비스의 통합 테스트를 작성할 때 다음과 같이 설정합니다.

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Testcontainers // Testcontainers 사용 활성화
@Import(FakeGcsServerTestConfig.class) // Fake GCS 설정 로드
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void uploadImageTest() {
        // 실제 Fake GCS Server가 동작하므로 파일 업로드 로직 테스트 가능
        // ...
    }
}
```

## 4. 주의사항

1. **Docker 실행 필수**: Testcontainers는 로컬에 Docker가 설치되어 있고 실행 중이어야 작동합니다. 테스트 실행 전 Docker Desktop 등을 켜주세요.
2. **초기 구동 시간**: 컨테이너 이미지를 다운로드하고 실행하는 데 시간이 걸릴 수 있습니다. (첫 실행 시 1분 내외 소요)
3. **포트 충돌 방지**: Testcontainers는 랜덤 포트를 매핑하므로 로컬 포트 충돌 걱정은 없습니다.

## 5. 트러블슈팅

### "Could not find a valid Docker environment" 에러
- Docker가 실행 중인지 확인하세요 (`docker ps`).
- Docker 권한 설정이 올바른지 확인하세요.

### "Table not found" 에러 (H2 Database)
- `application.yml`의 `ddl-auto` 설정이 `create-drop`으로 되어 있는지 확인하세요.
- Entity 클래스에 `@NoArgsConstructor`가 있는지 확인하세요. (JPA 필수)

