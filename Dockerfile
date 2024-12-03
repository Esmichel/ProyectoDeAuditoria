# Stage 1: Build the application using Maven
FROM maven:3.8.6-openjdk-18-slim AS builder

# Install dependencies for Maven and Git
RUN apt-get update && apt-get install -y \
    git \
    wget \
    unzip \
    && apt-get clean

# Set the working directory
WORKDIR /app

# Clone the GitHub repository
RUN git clone https://github.com/Esmichel/ProyectoDeAuditoria.git

# Set the working directory to the project directory
WORKDIR /app/ProyectoDeAuditoria/proyecto_ctf

# Ensure all dependencies are downloaded before building
RUN mvn dependency:go-offline -B

# Build and package the application (skip tests)
RUN mvn package

# Validate that the JAR file exists after build
RUN echo "Listing target directory contents:" && ls /app/ProyectoDeAuditoria/proyecto_ctf/target/

# Stage 2: Runtime environment with OpenJDK
FROM openjdk:18-slim

# Set the working directory for the app
WORKDIR /app

# Copy the built .jar file from the builder stage to the runtime stage
COPY --from=builder /app/ProyectoDeAuditoria/proyecto_ctf/target/proyecto_ctf-0.0.1-SNAPSHOT.jar /app/app.jar

# Make sure the JAR file is executable (optional)
RUN chmod +x /app/app.jar

# Expose necessary port for Spring Boot
EXPOSE 8080

# Command to run the Spring Boot application
CMD ["java", "-jar", "/app/app.jar"]
