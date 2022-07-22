FROM maven:3.8.4-openjdk-17-slim as builder

WORKDIR /home/app
COPY src ./src
COPY pom.xml .
RUN mvn -f ./pom.xml clean package

FROM openjdk:17.0-slim as runtime

COPY --from=builder /home/app/target/docker-spring-boot.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
