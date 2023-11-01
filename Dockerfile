FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/live-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-Dspring.datasource.url=jdbc:postgresql://host.docker.internal:5432/postgres", "-jar", "app.jar"]


#
#    log_format custom '$remote_addr - $remote_user [$time_local] "$request" '
#                          '$status $body_bytes_sent "$http_referer" '
#                          '"$http_user_agent" "$http_x_forwarded_for" '
#                          'upstream: $upstream_addr';
#            proxy_set_header X-Forwarded-For $proxy_host;
#FROM maven:3.8.6-ibm-semeru-17-focal
#
#ADD . /app
#WORKDIR /app
#
#RUN mvn clean install -DskipTests

#FROM openjdk:17-oracle
#
#LABEL name="ZEA"
#
#ARG JAR_FILE=/app/target/*.jar
#COPY --from=0 ${JAR_FILE} /app.jar
#ENTRYPOINT [ "java", "-jar", "app.jar" ]
