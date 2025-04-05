#FROM openjdk:17-jdk
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} techeer-resume-0.0.1-SNAPSHOT.jar
#ENTRYPOINT ["java","-jar","/techeer-resume-0.0.1-SNAPSHOT.jar"]

FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.jar
# 빌드 산출물을 /app.jar로 복사
COPY ${JAR_FILE} techeer-resume-0.0.1-SNAPSHOT.jar
# CI 파이프라인에서 다운로드한 newrelic 폴더 내의 파일들을 이미지에 포함
COPY newrelic/newrelic.jar /newrelic/newrelic.jar
COPY newrelic/newrelic.yml /newrelic/newrelic.yml
# javaagent 옵션 추가하여 NewRelic 에이전트 활성화
ENTRYPOINT ["java", "-javaagent:/newrelic/newrelic.jar", "-jar", "/techeer-resume-0.0.1-SNAPSHOT.jar"]
