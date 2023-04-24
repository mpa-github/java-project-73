FROM gradle:7.6.1-jdk17
# Vars for docker 'build' command arguments
ARG PROFILE
ARG PG_URL
ARG PG_USERNAME
ARG PG_PASSWORD
# Vars for container enviroment variables
ENV APP_PROFILE=${PROFILE}
ENV JDBC_DATABASE_URL=${PG_URL}
ENV JDBC_DATABASE_USERNAME=${PG_USERNAME}
ENV JDBC_DATABASE_PASSWORD=${PG_PASSWORD}
WORKDIR /usr/app/
COPY . .
RUN gradle stage
CMD SPRING_PROFILES_ACTIVE=prod build/install/app/bin/app

# Example Build stage

#FROM gradle:latest AS BUILD
#WORKDIR /usr/app/
#COPY . .
#RUN gradle build

# Example Package stage

#FROM openjdk:latest
#ENV JAR_NAME=app.jar
#ENV APP_HOME=/usr/app/
#WORKDIR $APP_HOME
#COPY --from=BUILD $APP_HOME .
#EXPOSE 8080
#ENTRYPOINT exec java -jar $APP_HOME/build/libs/$JAR_NAME

# Example from Spring doc

#FROM eclipse-temurin:17-jdk-alpine
#VOLUME /tmp
#ARG JAR_FILE
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
