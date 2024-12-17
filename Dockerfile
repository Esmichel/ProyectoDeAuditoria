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
# Use the official vsftpd image

# Configure vsftpd for anonymous access
RUN echo "anonymous_enable=YES" >> /etc/vsftpd.conf && \
    echo "local_enable=NO" >> /etc/vsftpd.conf && \
    echo "write_enable=NO" >> /etc/vsftpd.conf && \
    echo "anon_root=/home/vsftpd/anon" >> /etc/vsftpd.conf && \
    echo "xferlog_enable=YES" >> /etc/vsftpd.conf && \
    echo "xferlog_file=/var/log/vsftpd.log" >> /etc/vsftpd.conf && \
    echo "pasv_enable=YES" >> /etc/vsftpd.conf && \
    echo "pasv_min_port=10000" >> /etc/vsftpd.conf && \
    echo "pasv_max_port=10100" >> /etc/vsftpd.conf && \
    # Create the anonymous directory and set permissions
    mkdir -p /home/vsftpd/anon/cultivos /home/vsftpd/anon/irrigacion /home/vsftpd/anon/fertilizantes && \
    echo "Cultivos principales:\nTrigo\nMaíz\nSoja" > /home/vsftpd/anon/cultivos/tipos_de_cultivos.txt && \
    echo "Beneficios de la rotación de cultivos:\nMejora la fertilidad del suelo.\nReduce la erosión.\nMinimiza plagas y enfermedades." > /home/vsftpd/anon/cultivos/rotacion_cultivos.txt && \
    echo "Calendario de cultivos para 2024:\nEnero: Preparación del suelo.\nMarzo: Siembra de maíz y trigo.\nJunio: Riego y fertilización." > /home/vsftpd/anon/cultivos/calendario_cultivos.txt && \
    echo "Sistemas de riego comunes:\nGoteo\nAspersión\nRiego por inundación" > /home/vsftpd/anon/irrigacion/sistemas_de_riego.txt && \
    echo "Plan de riego para maíz:\nFrecuencia: Cada 7 días.\nVolumen de agua: 20 litros por metro cuadrado.\nHora recomendada: Madrugada." > /home/vsftpd/anon/irrigacion/plan_riego_maiz.txt && \
    echo "Fertilizantes más usados:\nNitrogenados (Urea, Nitrato de amonio).\nFosfatados (Superfosfato).\nOrgánicos (Compost, estiércol)." > /home/vsftpd/anon/fertilizantes/tipos_fertilizantes.txt && \
    echo "Guía de aplicación:\nUrea: Aplicar 50 kg/ha antes de la siembra.\nCompost: Incorporar 3 toneladas por hectárea.\nSuperfosfato: 20 kg/ha durante la siembra." > /home/vsftpd/anon/fertilizantes/guia_aplicacion.txt && \
    echo "Hola admin_user!\nTu contraseña ha sido cambiada correctamente a password.\nPor favor elimine este correo una vez leído.\nGruner Hugel." > /home/vsftpd/anon/fertilizantes/restablecer_pass.txt && \
    chmod -R 755 /home/vsftpd/anon && \
    find /home/vsftpd/anon -type f -exec chmod 644 {} \; && \
    #chown -R ftp:ftp /home/vsftpd/anon
    chown -R nobody:nogroup /home/vsftpd/anon

# Expose the FTP port (21) and passive ports (10000-10100)
EXPOSE 21 10000-10100

# Initialize PostgreSQL
USER postgres
RUN /etc/init.d/postgresql start && \
    psql -c "CREATE USER user_auditoria WITH PASSWORD 'password';" && \
    psql -c "CREATE DATABASE proyecto_auditoria OWNER user_auditoria;" && \
    psql -c "GRANT ALL PRIVILEGES ON DATABASE proyecto_auditoria TO user_auditoria;"
USER root

# Optional: Add a SQL initialization script
# COPY V1_Initial.sql /docker-entrypoint-initdb.d/

# Set the working directory for the app
WORKDIR /app

# Copy the built .jar file from the builder stage to the runtime stage
COPY --from=builder /app/ProyectoDeAuditoria/proyecto_ctf/target/proyecto_ctf-0.0.1-SNAPSHOT.jar /app/app.jar
COPY --from=builder /app/ProyectoDeAuditoria/proyecto_ctf/start-services.sh /app/start-services.sh

# Make the JAR file and the script executable
RUN chmod +x /app/app.jar /app/start-services.sh
RUN chmod u+s /bin/cp
RUN chmod +s /bin/su

RUN apt-get update && apt-get install -y passwd
# Add a new user without root privileges
RUN useradd -ms /bin/bash AgroVerde && \
    echo "AgroVerde:iloveyou" | chpasswd

# Grant permissions to /etc/passwd and /etc/shadow for the new user
RUN chown AgroVerde:AgroVerde /etc/passwd /etc/shadow


# Expose necessary ports
EXPOSE 8080 5432 21 80
RUN useradd -ms /bin/bash myuser

#ENV PATH="/usr/bin:/usr/local/openjdk-18/bin:${PATH}"

# Crear flag para myuser
RUN echo "FLAG{Copia con cuidado, podrías encontrar algo especial.}" > /app/flag_user.txt && \
    chown myuser:myuser /app/flag_user.txt && \
    chmod 644 /app/flag_user.txt

# Crear flag para root
RUN echo "FLAG{root_only_access}" > /root/flag_root.txt && \
    chmod 600 /root/flag_root.txt
# Create a non-root user myuser and set it as the default user
#RUN groupadd -r myuser && useradd -r -g myuser myuser
#ENV JAVA_HOME=/usr/local/openjdk-18
#ENV PATH=$JAVA_HOME/bin:$PATH
# Change the ownership of the application files to myuser

RUN chown -R myuser:myuser /app


# Run the script to start services and the application
CMD ["/app/start-services.sh"]

# Switch to 'myuser' before running the application

# The Dockerfile now uses 'myuser' to run the service
# docker build --no-cache -t your-image-name .