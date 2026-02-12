# Grafana Cloud APM 설정 가이드

배포 환경에서 Grafana Cloud + Alloy를 이용한 APM(Application Performance Monitoring) 및 k6 부하 테스트 설정 가이드입니다.

> **Note**: 이 프로젝트에는 이미 모니터링 설정이 완료되어 있습니다.
> - `monitoring/alloy/config.alloy` - Alloy 설정 파일
> - `docker-compose.yml` - Alloy 서비스 포함
> - `backend-secret.env` - Grafana Cloud 환경변수 설정 필요

## 목차

1. [아키텍처 개요](#아키텍처-개요)
2. [Grafana Cloud 설정](#grafana-cloud-설정)
3. [Alloy 설정 (메트릭/로그/트레이스 수집)](#alloy-설정)
4. [Spring Boot 설정](#spring-boot-설정)
5. [k6 부하 테스트](#k6-부하-테스트)
6. [Grafana 대시보드 활용](#grafana-대시보드-활용)

---

## 아키텍처 개요

```
┌─────────────────────────────────────────────────────────────┐
│                      배포 환경 (Server)                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │    MySQL     │  │    Redis     │  │   Backend    │       │
│  │              │  │              │  │ (Spring Boot)│       │
│  └──────────────┘  └──────────────┘  └──────┬───────┘       │
│                                             │               │
│                         metrics/logs/traces │               │
│                                             ▼               │
│                                    ┌──────────────┐         │
│                                    │    Alloy     │         │
│                                    │  (에이전트)   │         │
│                                    └──────┬───────┘         │
└───────────────────────────────────────────│─────────────────┘
                                            │
                                            ▼
                              ┌─────────────────────────┐
                              │     Grafana Cloud       │
                              │  ┌─────┐ ┌────┐ ┌─────┐ │
                              │  │Prom │ │Loki│ │Tempo│ │
                              │  └─────┘ └────┘ └─────┘ │
                              └─────────────────────────┘
                                            ▲
                                            │ 결과 업로드
┌───────────────────────────────────────────│─────────────────┐
│                      로컬 환경                               │
│                                    ┌──────────────┐         │
│                                    │     k6       │─────────┘
│                                    │  (부하 테스트) │
│                                    └──────────────┘
└─────────────────────────────────────────────────────────────┘
```

### 구성 요소 역할

| 구성 요소 | 역할 |
|-----------|------|
| **Alloy** | Grafana의 통합 수집 에이전트. 메트릭, 로그, 트레이스를 수집하여 Grafana Cloud로 전송 |
| **Prometheus** | 메트릭 저장소 (Grafana Cloud Metrics) |
| **Loki** | 로그 저장소 (Grafana Cloud Logs) |
| **Tempo** | 분산 트레이싱 저장소 (Grafana Cloud Traces) |
| **OpenTelemetry** | 트레이스 수집 표준 프로토콜 (Tempo에 저장) |
| **k6** | Grafana Labs의 부하 테스트 도구 |

---

## Grafana Cloud 설정

### 1. 계정 생성

1. [Grafana Cloud](https://grafana.com/products/cloud/) 접속
2. Free 플랜으로 가입 (14일 Pro 트라이얼 포함)

### 2. Credentials 수집

Grafana Cloud 포털에서 다음 정보를 수집합니다.

#### Stack 정보 확인

1. **My Account** → 본인의 **Stack** 선택
2. 각 서비스의 **Details** 클릭하여 정보 확인

| 환경변수 | 찾는 위치 |
|---------|----------|
| `GRAFANA_CLOUD_PROMETHEUS_URL` | Prometheus → Details → **Remote Write Endpoint** |
| `GRAFANA_CLOUD_PROMETHEUS_USERNAME` | Prometheus → Details → **Username** (숫자) |
| `GRAFANA_CLOUD_LOKI_URL` | Loki → Details → **URL** + `/loki/api/v1/push` |
| `GRAFANA_CLOUD_LOKI_USERNAME` | Loki → Details → **User** (숫자) |
| `GRAFANA_CLOUD_TEMPO_URL` | Tempo → Details → **Host** (포트 `:443` 포함) |
| `GRAFANA_CLOUD_TEMPO_USERNAME` | Tempo → Details → **User** (숫자) |

#### API Key 생성

1. **My Account** → **Access Policies** (또는 **API Keys**)
2. **Create Token** 클릭
3. 권한 설정:
   - `metrics:write`
   - `logs:write`
   - `traces:write`
4. 생성된 토큰 복사 (한 번만 표시됨)

### 3. 환경변수 파일 생성

`monitoring.env`:

```env
# Grafana Cloud API Key (모든 서비스에서 비밀번호로 사용)
GRAFANA_CLOUD_API_KEY=glc_xxxxxxxxxxxxxxxxxxxxxxxx

# Prometheus (Metrics)
GRAFANA_CLOUD_PROMETHEUS_URL=https://prometheus-prod-13-prod-us-east-0.grafana.net/api/prom/push
GRAFANA_CLOUD_PROMETHEUS_USERNAME=123456

# Loki (Logs)
GRAFANA_CLOUD_LOKI_URL=https://logs-prod-006.grafana.net/loki/api/v1/push
GRAFANA_CLOUD_LOKI_USERNAME=789012

# Tempo (Traces)
GRAFANA_CLOUD_TEMPO_URL=tempo-prod-04-prod-us-east-0.grafana.net:443
GRAFANA_CLOUD_TEMPO_USERNAME=345678
```

> **주의**: `monitoring.env`는 `.gitignore`에 추가하여 Git에 커밋되지 않도록 합니다.

---

## Alloy 설정

### 1. 디렉토리 구조

```
backend/
├── docker-compose.yml
├── monitoring/
│   └── alloy/
│       └── config.alloy
└── monitoring.env
```

### 2. Alloy 설정 파일

`monitoring/alloy/config.alloy`:

```hcl
// ==========================================
// Grafana Cloud 인증 설정
// ==========================================

// Prometheus Remote Write (Metrics)
prometheus.remote_write "grafana_cloud" {
  endpoint {
    url = env("GRAFANA_CLOUD_PROMETHEUS_URL")
    basic_auth {
      username = env("GRAFANA_CLOUD_PROMETHEUS_USERNAME")
      password = env("GRAFANA_CLOUD_API_KEY")
    }
  }
}

// Loki (Logs)
loki.write "grafana_cloud" {
  endpoint {
    url = env("GRAFANA_CLOUD_LOKI_URL")
    basic_auth {
      username = env("GRAFANA_CLOUD_LOKI_USERNAME")
      password = env("GRAFANA_CLOUD_API_KEY")
    }
  }
}

// Tempo (Traces) - OTLP Exporter
otelcol.auth.basic "grafana_cloud" {
  username = env("GRAFANA_CLOUD_TEMPO_USERNAME")
  password = env("GRAFANA_CLOUD_API_KEY")
}

otelcol.exporter.otlp "grafana_cloud" {
  client {
    endpoint = env("GRAFANA_CLOUD_TEMPO_URL")
    auth     = otelcol.auth.basic.grafana_cloud.handler
  }
}

// ==========================================
// Docker 컨테이너 Discovery
// ==========================================

discovery.docker "containers" {
  host             = "unix:///var/run/docker.sock"
  refresh_interval = "30s"
}

// ==========================================
// Spring Boot Actuator 메트릭 수집
// ==========================================

discovery.relabel "spring_boot" {
  targets = [{
    __address__ = "backend:8080",
  }]

  rule {
    target_label = "job"
    replacement  = "spring-boot"
  }

  rule {
    target_label = "instance"
    replacement  = "techeer-backend"
  }
}

prometheus.scrape "spring_boot" {
  targets         = discovery.relabel.spring_boot.output
  forward_to      = [prometheus.remote_write.grafana_cloud.receiver]
  metrics_path    = "/actuator/prometheus"
  scrape_interval = "15s"
}

// ==========================================
// Docker 컨테이너 로그 수집
// ==========================================

loki.source.docker "containers" {
  host       = "unix:///var/run/docker.sock"
  targets    = discovery.docker.containers.targets
  forward_to = [loki.write.grafana_cloud.receiver]

  relabel_rules = loki.relabel.docker_labels.rules
}

loki.relabel "docker_labels" {
  forward_to = []

  rule {
    source_labels = ["__meta_docker_container_name"]
    target_label  = "container"
  }

  rule {
    source_labels = ["__meta_docker_container_id"]
    target_label  = "container_id"
  }
}

// ==========================================
// OpenTelemetry 트레이스 수신
// ==========================================

otelcol.receiver.otlp "default" {
  grpc {
    endpoint = "0.0.0.0:4317"
  }
  http {
    endpoint = "0.0.0.0:4318"
  }

  output {
    traces = [otelcol.exporter.otlp.grafana_cloud.input]
  }
}
```

### 3. docker-compose.yml에 Alloy 추가

```yaml
services:
  # ... 기존 서비스들 ...

  # Grafana Alloy (Monitoring Agent)
  alloy:
    image: grafana/alloy:latest
    container_name: techeer-alloy
    restart: unless-stopped
    env_file:
      - monitoring.env
    ports:
      - "12345:12345"  # Alloy UI
      - "4317:4317"    # OTLP gRPC
      - "4318:4318"    # OTLP HTTP
    volumes:
      - ./monitoring/alloy/config.alloy:/etc/alloy/config.alloy
      - /var/run/docker.sock:/var/run/docker.sock:ro
    command:
      - run
      - /etc/alloy/config.alloy
      - --storage.path=/var/lib/alloy/data
      - --server.http.listen-addr=0.0.0.0:12345
    networks:
      - backend-network
```

### 4. Backend 서비스에 환경변수 추가

```yaml
services:
  backend:
    # ... 기존 설정 ...
    environment:
      - OTEL_EXPORTER_OTLP_ENDPOINT=http://alloy:4317
      - OTEL_SERVICE_NAME=techeer-backend
      - OTEL_METRICS_EXPORTER=none
```

---

## Spring Boot 설정

### 1. 의존성 추가

`build.gradle`:

```gradle
dependencies {
    // Actuator & Micrometer (Metrics)
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-registry-prometheus'

    // OpenTelemetry (Tracing)
    implementation 'io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter:2.4.0-alpha'
}
```

### 2. Application 설정

`application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  endpoint:
    health:
      show-details: always
  metrics:
    tags:
      application: techeer-backend
```

### 3. 실행 확인

```bash
# 서비스 실행
docker-compose up -d

# Alloy UI 확인
open http://localhost:12345

# Spring Boot Actuator 확인
curl http://localhost:8080/actuator/prometheus
```

---

## k6 부하 테스트

배포 환경의 부하 테스트는 **로컬에서 k6를 설치**하여 실행합니다.

### 1. 설치

```bash
# macOS
brew install k6

# Linux (Debian/Ubuntu)
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6

# Windows
choco install k6
```

### 2. Grafana Cloud 인증

```bash
# k6 Cloud Token은 Grafana Cloud → k6 → Settings에서 생성
k6 cloud login --token <YOUR_K6_CLOUD_TOKEN>
```

### 3. 테스트 스크립트 작성

`k6/load-test.js`:

```javascript
import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const apiDuration = new Trend('api_duration');

// 테스트 설정
export const options = {
  // 시나리오 기반 설정
  scenarios: {
    // 점진적 부하 증가
    ramping_load: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 10 },   // Warm-up
        { duration: '1m', target: 50 },    // 부하 증가
        { duration: '2m', target: 100 },   // 피크 부하
        { duration: '1m', target: 50 },    // 부하 감소
        { duration: '30s', target: 0 },    // Cool-down
      ],
      gracefulRampDown: '10s',
    },
  },

  // 성능 임계값
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'],  // 95%가 500ms, 99%가 1s 이내
    http_req_failed: ['rate<0.01'],                   // 실패율 1% 미만
    errors: ['rate<0.05'],                            // 에러율 5% 미만
  },
};

// 배포된 서버 URL (환경변수로 주입)
const BASE_URL = __ENV.BASE_URL || 'https://api.your-domain.com';

// 테스트 데이터
const testUser = {
  email: 'test@example.com',
  password: 'testPassword123',
};

export default function () {
  // Health Check
  group('Health Check', function () {
    const res = http.get(`${BASE_URL}/actuator/health`);
    check(res, {
      'health status 200': (r) => r.status === 200,
      'health check < 100ms': (r) => r.timings.duration < 100,
    });
  });

  // API 테스트
  group('Resume API', function () {
    // GET /api/v1/resumes
    const listRes = http.get(`${BASE_URL}/api/v1/resumes`, {
      headers: {
        'Content-Type': 'application/json',
      },
    });

    apiDuration.add(listRes.timings.duration);
    errorRate.add(listRes.status !== 200);

    check(listRes, {
      'list resumes status 200': (r) => r.status === 200,
      'list resumes < 500ms': (r) => r.timings.duration < 500,
    });
  });

  // 요청 간 간격
  sleep(Math.random() * 2 + 1); // 1-3초 랜덤 대기
}

// 테스트 완료 후 요약
export function handleSummary(data) {
  return {
    'stdout': textSummary(data, { indent: ' ', enableColors: true }),
  };
}

import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
```

### 4. 실행

```bash
# 로컬 실행 (터미널에서 결과 확인)
k6 run k6/load-test.js

# 환경변수로 URL 지정
k6 run -e BASE_URL=https://api.your-domain.com k6/load-test.js

# Grafana Cloud로 결과 업로드
k6 cloud run k6/load-test.js

# VU 수와 시간 직접 지정
k6 run --vus 50 --duration 2m k6/load-test.js
```

### 5. 테스트 시나리오 예시

#### Smoke Test (기본 동작 확인)

```javascript
export const options = {
  vus: 1,
  duration: '1m',
};
```

#### Load Test (일반 부하)

```javascript
export const options = {
  stages: [
    { duration: '5m', target: 100 },
    { duration: '10m', target: 100 },
    { duration: '5m', target: 0 },
  ],
};
```

#### Stress Test (한계 테스트)

```javascript
export const options = {
  stages: [
    { duration: '2m', target: 100 },
    { duration: '5m', target: 200 },
    { duration: '2m', target: 300 },
    { duration: '5m', target: 400 },
    { duration: '2m', target: 0 },
  ],
};
```

#### Spike Test (급격한 부하)

```javascript
export const options = {
  stages: [
    { duration: '10s', target: 100 },
    { duration: '1m', target: 100 },
    { duration: '10s', target: 1000 },  // 급격한 증가
    { duration: '3m', target: 1000 },
    { duration: '10s', target: 100 },
    { duration: '3m', target: 100 },
    { duration: '10s', target: 0 },
  ],
};
```

---

## Grafana 대시보드 활용

### 1. Explore에서 데이터 확인

| 데이터 타입 | 확인 방법 |
|------------|----------|
| **Metrics** | Explore → Prometheus → `spring_boot_*`, `jvm_*` 검색 |
| **Logs** | Explore → Loki → `{container="backend"}` 검색 |
| **Traces** | Explore → Tempo → Service 선택 |

### 2. 추천 대시보드 Import

Grafana Cloud에서 제공하는 대시보드를 Import합니다.

1. **Dashboards** → **Import**
2. Dashboard ID 입력:
   - `4701` - JVM (Micrometer)
   - `11378` - Spring Boot Statistics
   - `14430` - Spring Boot Observability
   - `18030` - k6 Results

### 3. 알림 설정

1. **Alerting** → **Alert rules** → **Create alert rule**
2. 조건 설정 예시:
   - Error rate > 5%
   - Response time p95 > 500ms
   - JVM memory usage > 80%

---

## 트러블슈팅

### Alloy가 시작되지 않을 때

```bash
# 로그 확인
docker logs techeer-alloy

# 설정 파일 문법 검사
docker run --rm -v $(pwd)/monitoring/alloy:/etc/alloy grafana/alloy:latest fmt /etc/alloy/config.alloy
```

### 메트릭이 수집되지 않을 때

```bash
# Spring Boot Actuator 확인
curl http://localhost:8080/actuator/prometheus

# Alloy 타겟 상태 확인
# Alloy UI (http://localhost:12345) → Targets
```

### 트레이스가 보이지 않을 때

```bash
# OTEL 환경변수 확인
docker exec backend env | grep OTEL

# Alloy OTLP 수신 확인
curl -X POST http://localhost:4318/v1/traces
```

---

## 참고 자료

- [Grafana Alloy 문서](https://grafana.com/docs/alloy/latest/)
- [k6 문서](https://k6.io/docs/)
- [OpenTelemetry Spring Boot](https://opentelemetry.io/docs/instrumentation/java/automatic/spring-boot/)
- [Micrometer Prometheus Registry](https://micrometer.io/docs/registry/prometheus)
