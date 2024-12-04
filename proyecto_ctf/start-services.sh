#!/bin/bash

# Start PostgreSQL
service postgresql start

until pg_isready -h localhost -p 5432 -U postgres; do
  echo "Esperando a PostgreSQL..."
  sleep 2
done

# Start Apache (optional)
#service apache2 start

# Start FTP service
#service vsftpd start

# Run the Spring Boot application
java -jar /app/app.jar
