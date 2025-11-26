# Spring Java Format 사용 가이드

## 개요

Spring Java Format은 Spring Framework의 공식 코드 포맷터로, Spring 프로젝트에서 일관된 코드 스타일을 유지하기 위해 사용됩니다.

## 적용 방법

### 1. Gradle을 통한 포맷팅

#### 모든 Java 파일 포맷팅 적용

```bash
./gradlew format
```

#### 포맷팅 검사만 수행 (적용하지 않음)

```bash
./gradlew checkFormat
```

#### main 소스만 포맷팅

```bash
./gradlew formatMain
```

#### test 소스만 포맷팅

```bash
./gradlew formatTest
```

### 2. IntelliJ IDEA 플러그인 설치

#### 플러그인 다운로드 및 설치

1. **플러그인 다운로드**

   - Maven Central에서 `spring-javaformat-intellij-idea-plugin` jar 파일 다운로드
   - 최신 버전: https://repo1.maven.org/maven2/io/spring/javaformat/spring-javaformat-intellij-idea/

2. **플러그인 설치**

   - IntelliJ IDEA 실행
   - `Preferences` (또는 `Settings`) → `Plugins` 이동
   - 우측 상단의 톱니바퀴 아이콘 클릭
   - `Install Plugin from Disk...` 선택
   - 다운로드한 jar 파일 선택
   - IntelliJ IDEA 재시작

3. **플러그인 활성화 확인**
   - 프로젝트에 `.springjavaformatconfig` 파일이 있거나
   - `build.gradle`에 `io.spring.javaformat` 플러그인이 적용되어 있으면
   - 상태 바에 Spring Java Format 아이콘(🌱)이 표시됩니다

### 3. IntelliJ IDEA에서 포맷팅 사용

#### 수동 포맷팅

- **Mac**: `Cmd + Option + L`
- **Windows/Linux**: `Ctrl + Alt + L`
- 또는 `Code` → `Reformat Code` 메뉴 선택

#### 저장 시 자동 포맷팅 설정

1. `Preferences` → `Tools` → `Actions on Save` 이동
2. `Reformat code` 옵션 체크
3. `Run cleanup code` 옵션도 함께 체크 가능

#### 특정 코드 블록 포맷팅 제외

```java
// @formatter:off
// 이 블록은 포맷팅되지 않습니다
public void complexMethod() {
    // 복잡한 설정 코드
}
// @formatter:on
```

## 적용되는 포맷팅 규칙

### 1. 들여쓰기 (Indentation)

- **기본값**: 탭(Tab) 사용
- **스페이스 사용 설정**: 프로젝트 루트에 `.springjavaformatconfig` 파일 생성
  ```
  indentation-style=spaces
  ```

### 2. 줄 길이 (Line Length)

- **최대 줄 길이**: 120자
- 긴 줄은 자동으로 줄바꿈 처리

### 3. 중괄호 스타일 (Brace Style)

- K&R 스타일 사용
- 여는 중괄호는 같은 줄에, 닫는 중괄호는 별도 줄

```java
// 올바른 예
public void method() {
    // 코드
}

// 잘못된 예
public void method()
{
    // 코드
}
```

### 4. 공백 (Whitespace)

- 메서드 본문 내 공백 줄 제거 권장
- 메서드 간에는 공백 줄 유지
- 연산자 주변 공백 자동 조정

### 5. 임포트 (Imports)

- 와일드카드 임포트(`import java.util.*;`) 금지
- 정렬 및 정리 자동 수행

### 6. 주석 (Comments)

- Javadoc 형식 준수
- 공개 클래스와 메서드에 Javadoc 작성 권장

### 7. 최종 키워드 (Final)

- private 필드는 가능한 경우 `final` 사용
- 로컬 변수와 파라미터는 일반적으로 `final` 생략

### 8. 메서드 및 필드 순서

- 읽기 쉬운 순서로 배치 (위에서 아래로 읽기)
- private 메서드는 호출하는 메서드 근처에 배치

## 포맷팅 적용 시점

### 자동 적용

- `./gradlew format` 실행 시
- IntelliJ IDEA에서 `Reformat Code` 실행 시
- 저장 시 자동 포맷팅 설정 시

### 수동 적용

- Gradle 태스크 실행
- IntelliJ IDEA 단축키 사용

## 주의사항

1. **생성된 코드 제외**

   - `src/main/generated` 디렉토리는 자동으로 제외됩니다
   - QueryDSL 등 생성된 코드는 포맷팅 대상이 아닙니다

2. **Java 버전**

   - 기본적으로 Java 17 이상 필요
   - Java 8 사용 시 프로젝트 루트에 `.springjavaformatconfig` 파일 생성

   ```
   java-baseline=8
   ```

3. **CI/CD 통합**
   - `./gradlew checkFormat`을 CI 파이프라인에 추가하여
   - 포맷팅 규칙 준수 여부를 자동으로 검사할 수 있습니다

## 참고 자료

- [Spring Java Format 공식 저장소](https://github.com/spring-io/spring-javaformat)
- [Spring Java Format Maven Central](https://repo1.maven.org/maven2/io/spring/javaformat/)
