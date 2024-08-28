FROM amazoncorretto:21-alpine

WORKDIR /bff

COPY rest/target/rest-0.0.1-SNAPSHOT.jar /bff/bff-docker.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/bff/bff-docker.jar"]