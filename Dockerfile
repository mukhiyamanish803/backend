# Stage 1: Build with Maven
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY employara/pom.xml .
COPY employara/src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Extract layers to /app/extracted
FROM eclipse-temurin:21-jdk-alpine AS extractor
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher --destination /app/extracted

# Stage 3: Runtime image
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy all extracted layer directories into /app
COPY --from=extractor /app/extracted/dependencies/ ./
COPY --from=extractor /app/extracted/spring-boot-loader/ ./
COPY --from=extractor /app/extracted/snapshot-dependencies/ ./
COPY --from=extractor /app/extracted/application/ ./

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
