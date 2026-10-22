# Stage 1: Builder (compile & package the app)
FROM eclipse-temurin:22-jdk-jammy AS builder

WORKDIR /build

# Copy Maven wrapper & config
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN ./mvnw clean package -DskipTests \
    && mv target/*.jar /build/app.jar


# Stage 2: Runtime (small, secure production image)
FROM eclipse-temurin:22-jre-jammy

# Create non-root user & group
RUN groupadd -g 1000 spring && \
    useradd -u 1000 -g spring -m -s /bin/bash spring

WORKDIR /app

# Copy only the final JAR from builder
COPY --from=builder --chown=spring:spring /build/app.jar app.jar

USER spring:spring

EXPOSE 8081

ENV JAVA_OPTS="\
    -XX:InitialRAMPercentage=75.0 \
    -XX:MaxRAMPercentage=80.0 \
    -XX:+UseContainerSupport \
    -XX:+UseG1GC \
    -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT exec java $JAVA_OPTS -Dspring.profiles.active=docker -jar /app/app.jar