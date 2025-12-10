# Stage 1: Build with Maven
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Copy source and pom
COPY pom.xml .
COPY src ./src

# Build the jar
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8083

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]