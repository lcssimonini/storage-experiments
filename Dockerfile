FROM openjdk:17-jdk-alpine

RUN mkdir /application
WORKDIR /application

COPY target/*.jar ./app.jar
COPY newrelic/* ./

EXPOSE 8080

ARG JAVA_ADDITIONAL_OPTS
ENV JAVA_ADDITIONAL_OPTS=$JAVA_ADDITIONAL_OPTS

ENTRYPOINT java $JAVA_ADDITIONAL_OPTS \
  -javaagent:newrelic.jar \
  -Dnewrelic.environment=$SPRING_PROFILES_ACTIVE \
  -Djava.security.egd=file:/dev/./urandom \
  -jar app.jar
