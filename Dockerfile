# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the build output (JAR file) from the host to the container
COPY target/redis-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8181 (mapped to 7171 on the host via docker-compose)
EXPOSE 7171

# Set environment variables for JVM optimization
ENV JAVA_OPTS="-Xms512m -Xmx1536m"

# Command to run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
