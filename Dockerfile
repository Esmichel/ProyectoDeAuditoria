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
RUN mvn package -P prod -DskipTests -X 

# Validate that the JAR file exists after build
RUN echo "Listing target directory contents:" && ls /app/ProyectoDeAuditoria/proyecto_ctf/target/

# Stage 2: Runtime environment with OpenJDK
FROM openjdk:18-slim

# Install PostgreSQL and other necessary dependencies
RUN apt-get update && apt-get install -y \
    postgresql postgresql-contrib \
    vsftpd \
    apache2 \
    nano \
    && apt-get clean

RUN echo "listen_addresses='*'" >> /etc/postgresql/13/main/postgresql.conf
# permitir conexiones desde cualquier ip 
RUN echo "host    proyecto_auditoria    user_auditoria    0.0.0.0/0    md5" >> /etc/postgresql/13/main/pg_hba.conf 

# Configurar vsftpd para acceso anónimo
RUN echo "anonymous_enable=YES" >> /etc/vsftpd.conf && \
    echo "local_enable=NO" >> /etc/vsftpd.conf && \
    echo "write_enable=NO" >> /etc/vsftpd.conf && \
    echo "anon_root=/home/vsftpd/anon" >> /etc/vsftpd.conf && \
    echo "xferlog_enable=YES" >> /etc/vsftpd.conf && \
    echo "xferlog_file=/var/log/vsftpd.log" >> /etc/vsftpd.conf && \
    echo "pasv_enable=YES" >> /etc/vsftpd.conf && \
    echo "pasv_min_port=10000" >> /etc/vsftpd.conf && \
    echo "pasv_max_port=10100" >> /etc/vsftpd.conf && \
    mkdir -p /home/vsftpd/anon && \
    echo "Archivo de prueba" > /home/vsftpd/anon/test.txt && \
    chmod -R 755 /home/vsftpd/anon && \
    chmod 644 /home/vsftpd/anon/test.txt && \
    chown -R ftp:ftp /home/vsftpd/anon

# Initialize PostgreSQL
USER postgres
RUN /etc/init.d/postgresql start && \
    psql -c "CREATE USER user_auditoria WITH PASSWORD 'password';" && \
    psql -c "CREATE DATABASE proyecto_auditoria OWNER user_auditoria;" && \
    psql -c "GRANT ALL PRIVILEGES ON DATABASE proyecto_auditoria TO user_auditoria;"
USER root

# Optional: Add a SQL initialization script
# COPY V1_Initial.sql /docker-entrypoint-initdb.d/

# Después de instalar las herramientas necesarias en la segunda etapa
RUN chmod u+s /bin/cp

# Set the working directory for the app
WORKDIR /app

# Copy the built .jar file from the builder stage to the runtime stage
COPY --from=builder /app/ProyectoDeAuditoria/proyecto_ctf/target/proyecto_ctf-0.0.1-SNAPSHOT.jar /app/app.jar
COPY --from=builder /app/ProyectoDeAuditoria/proyecto_ctf/start-services.sh /app/start-services.sh


# Make the JAR file and the script executable
RUN chmod +x /app/app.jar /app/start-services.sh

# Expose necessary ports
EXPOSE 8080 5432 21 80

# Run the script to start services and the application
CMD ["/app/start-services.sh"]


# para cuando se hacen cambios en el dockerfile y hay que volver a compilar limpio 
# docker build --no-cache -t your-image-name .