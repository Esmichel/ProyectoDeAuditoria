#!/bin/bash

# Start PostgreSQL
service postgresql start

# Start Apache (optional)
#service apache2 start

# Start FTP service
service vsftpd start
su myuser 
# Run the Spring Boot application
java -jar /app/app.jar
