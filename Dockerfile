# Stage 1: Build the application using Maven
FROM maven:3.8.5-openjdk-17 AS build

# Copy the project files to the container
WORKDIR /app
COPY . .

# Build the project and create the executable JAR
RUN mvn clean install

# Stage 2: Create the final, lightweight runtime image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the executable JAR from the build stage
COPY --from=build /app/target/affiliate-agent-0.0.1-SNAPSHOT.jar affiliate-agent.jar

# Expose the port the application runs on
EXPOSE 8085

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "affiliate-agent.jar"]
