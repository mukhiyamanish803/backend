FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY employara/pom.xml .
COPY employara/src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jdk-alpine AS extractor
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# 📌 Correct syntax here:
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=extractor /app/dependencies/ ./
COPY --from=extractor /app/spring-boot-loader/ ./
COPY --from=extractor /app/snapshot-dependencies/ ./
COPY --from=extractor /app/application/ ./

EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
