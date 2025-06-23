# 🎯 Stage 1: Build with Maven & JDK 21
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies
COPY pom.xml ./employara
RUN mvn dependency:go-offline -B

# Build the app
COPY src ./employara/src
RUN mvn clean package -DskipTests -B

# 🧩 Stage 2: Extract Spring Boot layers
FROM eclipse-temurin:21-jdk-alpine AS extractor
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher

# 🚀 Stage 3: Final runtime image with JRE 21
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy optimized layers
COPY --from=extractor /app/dependencies/ ./dependencies/
COPY --from=extractor /app/spring-boot-loader/ ./spring-boot-loader/
COPY --from=extractor /app/snapshot-dependencies/ ./snapshot-dependencies/
COPY --from=extractor /app/application/ ./application/

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
