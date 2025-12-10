# ------------------------
# Stage 1: Build with Maven
# ------------------------
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the jar
RUN mvn clean package -DskipTests

# ------------------------
# Stage 2: Runtime
# ------------------------
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port for the service
EXPOSE 8081

# Command to run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]