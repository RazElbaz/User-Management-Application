FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the project
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/user-management-0.0.1-SNAPSHOT.jar app.jar
# Expose port 8080
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
