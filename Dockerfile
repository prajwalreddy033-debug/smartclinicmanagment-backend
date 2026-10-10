# ==========================================
# Stage 1: Build JAR using Maven and Java 21
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven project descriptor
COPY pom.xml .

# Copy application source code
COPY src ./src

# Build production JAR (skip unit tests during build)
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Minimal Runtime Environment
# ==========================================
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create upload directories
RUN mkdir -p uploads/lab-reports

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Render injects the PORT environment variable dynamically
ENV PORT=8070
EXPOSE 8070

# Run Spring Boot application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
