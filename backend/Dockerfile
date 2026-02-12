# 1단계: 빌드용 이미지
FROM gradle:8.6-jdk21 AS build
WORKDIR /app

# 소스코드 전체 복사 (gradle/wrapper 포함)
COPY . .

# JAR 빌드 (bootJar, 테스트 및 asciidoctor 제외)
RUN gradle bootJar -x test -x asciidoctor --no-daemon

# 2단계: 실행용 이미지
FROM eclipse-temurin:21-jdk
WORKDIR /app

# 빌드 결과물만 복사
COPY --from=build /app/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=docker"]
