# --------- ETAPA 1: BUILD ----------
FROM maven:3.9.9-eclipse-temurin-17 AS api
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn clean install -DskipTests


# --------- ETAPA 2: RUNTIME ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=API /workspace/target/*.jar api-gateway.jar

EXPOSE 8001

ENTRYPOINT ["java","-jar","api-gateway.jar"]
