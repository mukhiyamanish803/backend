# Stage 1 — Build
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY employara/pom.xml .
COPY employara/src ./src
RUN mvn clean package -DskipTests -B

# Stage 2 — Extract layers
FROM eclipse-temurin:21-jdk-alpine AS extractor
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# ☑️ Use updated, non-deprecated extract command
RUN java -Djarmode=tools extract --layers --launcher -jar app.jar

# Stage 3 — Runtime image
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Combine all extracted layers into /app
COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/ ./

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
