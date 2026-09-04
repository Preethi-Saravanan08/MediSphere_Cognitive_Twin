# Build Stage
FROM maven:3.9.0-eclipse-temurin-25 AS builder

WORKDIR /build

# Copy pom.xml
COPY pom.xml .

# Download dependencies
RUN mvn dependency:resolve

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:25-jdk-alpine

WORKDIR /app

# Copy built JAR from builder
COPY --from=builder /build/target/medisphere-cognitive-twin-1.0.0.jar app.jar

# Create non-root user
RUN addgroup -g 1000 medisphere && adduser -D -u 1000 -G medisphere medisphere
USER medisphere

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/api/health || exit 1

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
