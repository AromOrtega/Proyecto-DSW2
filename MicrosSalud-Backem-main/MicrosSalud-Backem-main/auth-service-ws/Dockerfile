# Etapa 1: Build
FROM maven:3.9.9-eclipse-temurin-17 AS auth
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=auth /workspace/target/*.jar auth-service.jar
# EXPOSE numero de puerto
ENTRYPOINT ["java","-jar","auth-service.jar"]