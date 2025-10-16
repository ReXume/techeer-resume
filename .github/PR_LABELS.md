# PR 라벨 가이드

이 문서는 Pull Request에 라벨을 지정하여 CI를 선택적으로 실행하는 방법을 설명합니다.

## 🚨 중요: 라벨 기반 CI 시스템

**PR 생성 시에는 아무런 CI가 실행되지 않습니다.**  
CI를 실행하려면 반드시 해당 라벨을 추가해야 합니다.

## 🏷️ 사용 가능한 CI 라벨

### 기본 라벨

- **`ci:backend`** - 백엔드 관련 CI 실행

  - Backend Compile
  - Backend Test
  - Backend Lint
  - Backend Security

- **`ci:frontend`** - 프론트엔드 관련 CI 실행

  - Frontend Compile
  - Frontend Lint
  - Frontend Test
  - Frontend Type Check

- **`ci:integration`** - 통합 테스트 실행

  - Integration Test (Docker Compose)

- **`ci:docker`** - Docker 빌드 테스트 실행
  - Docker Build Test

### 우선순위 라벨

- **`ci:all`** - 모든 CI 실행 (기본값)
- **`ci:quick`** - 빠른 검증만 (컴파일 + 린트)
- **`ci:full`** - 전체 테스트 실행 (테스트 + 보안 검사 포함)

## 🚀 PR 워크플로우 기반 사용법

### 1. PR 생성 시

**아무런 CI가 실행되지 않습니다.** 코드 리뷰를 위해 PR을 먼저 생성하세요.

### 2. 코드 리뷰 단계

기본적인 코드 품질 검사를 위해 라벨을 추가합니다.

```
ci:quick    # 빠른 검증 (컴파일 + 린트만, ~3분)
ci:backend  # 백엔드만 테스트
ci:frontend # 프론트엔드만 테스트
```

### 3. 테스트 단계

코드 리뷰가 완료되면 본격적인 테스트를 실행합니다.

```
ci:full     # 전체 테스트 + 보안 검사 실행 (~15분)
ci:all      # 모든 워크플로우 실행 (~10분)
```

### 4. 머지 직전 단계

최종 검증을 위해 무거운 테스트들을 실행합니다.

```
ci:docker       # Docker 빌드 테스트
ci:integration  # 통합 테스트 (Docker Compose)
```

## 📋 PR 단계별 라벨 전략

### 🔍 코드 리뷰 단계 (PR 생성 직후)

```
ci:quick    # 빠른 코드 품질 검사만
```

### 🧪 테스트 단계 (리뷰 완료 후)

```
ci:backend  # 백엔드 개발자
ci:frontend # 프론트엔드 개발자
ci:all      # 풀스택 개발자
```

### 🔒 최종 검증 단계 (머지 직전)

```
ci:full         # 전체 테스트 + 보안 검사
ci:docker       # Docker 빌드 검증
ci:integration  # 통합 테스트
```

### 🚀 배포 준비 단계

```
ci:all
ci:full
ci:docker
ci:integration
```

## ⚡ CI 실행 시간 예상

| 라벨          | 예상 시간 | 실행되는 워크플로우         |
| ------------- | --------- | --------------------------- |
| `ci:quick`    | ~3분      | 컴파일 + 린트 (4개)         |
| `ci:backend`  | ~8분      | 백엔드 전체 (4개)           |
| `ci:frontend` | ~5분      | 프론트엔드 전체 (4개)       |
| `ci:all`      | ~10분     | 모든 워크플로우 (8개)       |
| `ci:full`     | ~15분     | 모든 워크플로우 + 보안 검사 |

## 🔧 라벨 추가/제거 방법

1. PR 페이지에서 "Labels" 섹션 클릭
2. 원하는 라벨 선택/해제
3. 라벨 변경 시 자동으로 CI 재실행

## 💡 PR 워크플로우 팁

### 📝 PR 생성 시

1. **먼저 PR을 생성**하고 코드 리뷰 요청
2. **라벨 없이** 코드 리뷰 진행
3. 리뷰어가 코드를 확인한 후 적절한 라벨 추가

### 🔄 단계별 진행

1. **코드 리뷰**: `ci:quick` 추가
2. **테스트**: `ci:backend` 또는 `ci:frontend` 추가
3. **최종 검증**: `ci:full`, `ci:docker` 추가
4. **머지**: 모든 CI 통과 후 머지

### ⚡ 효율적인 사용법

- **작은 수정**: `ci:quick`만 사용
- **기능 개발**: `ci:backend`/`ci:frontend` → `ci:full`
- **인프라 변경**: `ci:docker` → `ci:integration`
- **배포 전**: 모든 라벨 추가

## 🚨 주의사항

- **라벨이 없으면 CI가 실행되지 않습니다**
- **라벨을 제거하면 해당 CI가 중단됩니다**
- **PR 머지 조건에 CI 통과를 필수로 설정하세요**
- **단계별로 라벨을 추가하여 점진적으로 검증하세요**
