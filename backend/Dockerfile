#FROM openjdk:17-jdk
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} techeer-resume-0.0.1-SNAPSHOT.jar
#ENTRYPOINT ["java","-jar","/techeer-resume-0.0.1-SNAPSHOT.jar"]

FROM openjdk:17-jdk
WORKDIR /app

# 빌드 결과물 복사 (bootJar 작업물)
COPY backend/build/libs/*.jar /app/app.jar

# 빌드 단계에서 만든 newrelic 디렉토리 복사
COPY newrelic/newrelic.jar /app/newrelic/
COPY newrelic/newrelic.yml /app/newrelic/

# 자주 쓰는 환경 변수 설정 (옵션)
ENV NEW_RELIC_APP_NAME="techeer-resume"
ENV NEW_RELIC_LOG_LEVEL="info"

# -javaagent 옵션으로 New Relic Java Agent 활성화
ENTRYPOINT ["java", "-javaagent:/app/newrelic/newrelic.jar", "-jar", "/app/app.jar"]
