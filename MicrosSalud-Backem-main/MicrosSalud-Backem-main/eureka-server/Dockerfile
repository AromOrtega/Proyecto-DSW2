# ====== Build ======
FROM maven:3.9.9-eclipse-temurin-17 AS eureka
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# ====== Run ======
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=eureka /app/target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java","-jar","app.jar"]
