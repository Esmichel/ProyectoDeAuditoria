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
RUN mvn clean install package -DskipTests

# Stage 2: Runtime environment with OpenJDK and dependencies
FROM openjdk:24-ea-17-slim-bullseye

# Install runtime dependencies like Apache, MySQL, PHP, etc.
RUN apt-get update && apt-get install -y \
    apache2 \
    php \
    libapache2-mod-php \
    default-mysql-server \
    php-mysql \
    vsftpd \
    nano \
    cron \
    wget \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Set the working directory for the app
WORKDIR /app

# Copy the built .jar file from the builder stage to the runtime stage
COPY --from=builder /app/ProyectoDeAuditoria/proyecto_ctf/target/*.jar /app/app.jar

# Configure Apache server (create a test PHP file)
RUN echo "<?php phpinfo(); ?>" > /var/www/html/info.php

# Set up VSFTPD directory
RUN mkdir -p /var/ftp && chmod 777 /var/ftp

# Create and configure users with permissions
RUN useradd -m user1 && echo "user1:password1" | chpasswd && \
    useradd -m user2 && echo "user2:password2" | chpasswd && \
    groupadd admin_group && usermod -aG admin_group user2 && \
    echo 'user2 ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

# Configure cronjobs (example jobs)
RUN echo "*/1 * * * * root /bin/echo 'Este es un cron job de ejemplo'" > /etc/cron.d/example-cron && \
    echo "* * * * * root /usr/bin/id > /tmp/cron_output.txt" > /etc/cron.d/cron_vuln && \
    chmod 0644 /etc/cron.d/example-cron /etc/cron.d/cron_vuln

# Add a script for potential vulnerability
RUN echo -e "#!/bin/bash\nchmod 4755 /bin/bash" > /root/set_suid.sh && chmod +x /root/set_suid.sh

# Expose necessary ports
EXPOSE 8080 80 21

# Command to start services and run the application
CMD service apache2 start && service vsftpd start && service cron start && java -jar /app/app.jar
