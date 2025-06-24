# Dockerfile for Spring Boot Application

# Use a multi-stage build to create a smaller final image

# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copy the Maven wrapper files and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (to leverage Docker layer caching)
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B

# Copy the rest of the application source code
COPY src src

# Build the JAR file
RUN ./mvnw package -DskipTests

# Stage 2: Create the final lean image
FROM eclipse-temurin:17-jre-jammy AS final
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Optional: Add metadata for maintainability
LABEL maintainer="Zhaofu Liu <thlzhf00@sina.com>"
LABEL version="1.0"
LABEL description="Electric Mobility Charging Station Information Management System"
