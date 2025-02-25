# Java 21을 사용하는 Eclipse Temurin JDK 기반 이미지
FROM eclipse-temurin:21-jdk-alpine

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사 (Gradle 빌드 후)
COPY ./build/libs/*.jar app.jar

# 컨테이너 실행 시 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]