# Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven build output (JAR file) to the container
COPY target/marketplace-api-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application runs on
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]