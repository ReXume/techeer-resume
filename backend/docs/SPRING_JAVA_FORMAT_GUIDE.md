# Spring Boot & Java 코드 스타일 가이드

## 1. 개요

본 프로젝트는 코드의 일관성과 가독성을 유지하기 위해 **우아한형제들 기술 캠프의 Java 코드 컨벤션**을 따릅니다. IntelliJ IDEA에서는 `Woowahan Style` 플러그인을 사용하여 이 컨벤션을 손쉽게 적용할 수 있습니다.

이 문서는 플러그인 설치 및 설정 방법과 함께, 프로젝트에서 따르는 주요 코딩 스타일 및 모범 사례를 안내합니다.

## 2. IntelliJ 플러그인 설치 및 설정

### 2.1. Woowahan Style 플러그인 설치

1.  **IntelliJ 설정 열기**:
    *   macOS: `IntelliJ IDEA` -> `Settings` (단축키: `Cmd + ,`)
    *   Windows/Linux: `File` -> `Settings`
2.  **플러그인 마켓플레이스 이동**:
    *   좌측 메뉴에서 `Plugins` 선택 후, 상단의 `Marketplace` 탭으로 이동합니다.
3.  **플러그인 검색 및 설치**:
    *   검색창에 `Woowahan Style`을 입력하고 검색합니다.
    *   `Checkstyle-IDEA`와 `Woowahan-Style` 두 가지가 나올 수 있으나, 코드 포맷팅을 위해서는 **`Woowahan-Style`**을 설치합니다.
    *   `Install` 버튼을 클릭하여 설치하고, 설치가 완료되면 IntelliJ를 재시작합니다.

### 2.2. 코드 스타일 적용

1.  **코드 스타일 설정 이동**:
    *   `Settings` -> `Editor` -> `Code Style`로 이동합니다.
2.  **Scheme 변경**:
    *   `Scheme` 드롭다운 메뉴를 클릭하여 **`Woowahan-Style`**을 선택합니다.
    *   `Apply` 또는 `OK` 버튼을 눌러 프로젝트의 기본 코드 스타일로 저장합니다.

### 2.3. 코드 자동 포맷팅 설정

코드 작성 후 수동으로 포맷팅을 적용할 수도 있지만, 저장 시 자동으로 포맷팅되도록 설정하면 편리합니다.

1.  **Actions on Save 설정 이동**:
    *   `Settings` -> `Tools` -> `Actions on Save`로 이동합니다.
2.  **자동 포맷팅 활성화**:
    *   `Reformat code` 옵션을 체크합니다.
    *   (선택) `Optimize imports` 옵션을 체크하면 사용하지 않는 import 문을 자동으로 제거하고 순서를 정리해줍니다.

## 3. 주요 코드 스타일 및 모범 사례

### 3.1. 네이밍 컨벤션 (Naming Convention)

-   **클래스, 인터페이스, Enum**: `PascalCase` (e.g., `ImageController`, `UserService`, `OrderStatus`)
-   **메서드, 변수**: `camelCase` (e.g., `findUserById`, `userName`)
-   **상수**: `UPPER_SNAKE_CASE` (e.g., `MAX_LOGIN_ATTEMPTS`)
-   **테스트 메서드**: `[given]_[when]_[then]` 형식을 권장합니다. (e.g., `givenUserExists_whenRequestingUserInfo_thenReturnsUserInfo`)

### 3.2. 패키지 구조 (Package Structure)

-   본 프로젝트는 **Hexagonal Architecture**를 따르며, 패키지 구조는 다음과 같이 구성됩니다.
    -   `com.techeer.backend.domain`: 핵심 도메인 엔티티
    -   `com.techeer.backend.application`: UseCase 및 Port 인터페이스
    -   `com.techeer.backend.adapter`: Controller, Persistence 등 외부 기술 구현체

### 3.3. DTO (Data Transfer Object)

-   **Request/Response 분리**: Controller 계층에서는 반드시 Request DTO와 Response DTO를 사용하여 데이터를 주고받습니다. **절대로 Domain Entity를 직접 반환하지 않습니다.**
-   **레코드(Record) 활용**: 데이터 불변성을 보장하고 보일러플레이트 코드를 줄이기 위해, 단순 데이터 전달 목적의 DTO는 Java 16 이상에서 제공하는 `record` 사용을 적극 권장합니다.

    ```java
    // Good
    public record UserInfoResponse(Long id, String email, String username) {
    }
    ```

### 3.4. 컨트롤러 (Controller)

-   **책임 최소화**: 컨트롤러는 HTTP 요청을 받아 적절한 UseCase(Service)를 호출하고, 그 결과를 DTO로 변환하여 반환하는 역할만 수행합니다. **비즈니스 로직을 포함해서는 안 됩니다.**
-   **명시적인 Annotation 사용**: `@RequestMapping` 대신 `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` 등 명시적인 어노테이션을 사용합니다.
-   **일관된 응답 형식**: 모든 API 응답은 `ApiResponse` 클래스로 감싸서 일관된 형식(성공 여부, 메시지, 데이터)을 유지합니다.

### 3.5. 주석 (Comments)

-   **'무엇'이 아닌 '왜'**: 코드가 *무엇을* 하는지에 대한 주석보다는, *왜* 그렇게 작성되었는지, 특정 비즈니스 결정이나 기술적 트레이드오프가 있었는지를 설명하는 주석을 작성합니다.
-   **Javadoc**: 외부에 노출되는 공개 API(Controller 메서드 등)나 복잡한 로직을 가진 핵심 public 메서드에는 Javadoc을 작성하여 API 명세를 명확히 합니다.

## 4. 포맷팅 실행

-   **단축키 (수동 실행)**:
    *   macOS: `Cmd + Option + L`
    *   Windows/Linux: `Ctrl + Alt + L`
-   **파일 저장 시 (자동 실행)**:
    *   위 `2.3` 설정이 완료된 경우, 파일을 저장(`Cmd + S` 또는 `Ctrl + S`)할 때마다 자동으로 포맷팅이 적용됩니다.
