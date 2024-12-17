#!/bin/bash

# Start PostgreSQL
service postgresql start

# Start Apache (optional)
#service apache2 start

# Start FTP service
service vsftpd start

su - myuser -c "export JAVA_HOME=/usr/local/openjdk-18 && export PATH=$JAVA_HOME/bin:$PATH && java -jar /app/app.jar"

# Run the Spring Boot application as myuser
#java -jar /app/app.jar
