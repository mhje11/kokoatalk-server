# Base image for Java 21
FROM eclipse-temurin:21-jdk-jammy

# Set working directory in the container
WORKDIR /app

# Copy the JAR file to the container
COPY build/libs/kokoatalk-server-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
