#!/bin/bash

# Set Java environment variables
export JAVA_HOME=/usr/lib/jvm/java-18-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Start PostgreSQL
service postgresql start

# Start Apache (optional)
#service apache2 start

# Start FTP service
service vsftpd start


# Run the Spring Boot application as myuser
su - myuser -c "java -jar /app/app.jar"
