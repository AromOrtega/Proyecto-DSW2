FROM maven:3.9.9-eclipse-temurin-17 AS confi

WORKDIR /workspace

COPY src ./src
COPY pom.xml ./pom.xml

RUN mvn clean install -DskipTests


FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=confi /workspace/target/*.jar config-server.jar

RUN mkdir /app/config-repo

COPY ./config-repo/ ./config-repo/
RUN chmod -R 755 /app/config-repo

EXPOSE 8888
ENTRYPOINT [ "sh", "-c","java -jar config-server.jar" ]
