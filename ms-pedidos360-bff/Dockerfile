FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# El puerto es informativo, Docker Compose maneja el mapeo real
EXPOSE 8080 
ENTRYPOINT ["java", "-jar", "app.jar"]