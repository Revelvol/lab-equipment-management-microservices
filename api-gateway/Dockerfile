FROM openjdk:17

COPY target/*.jar app.jar


EXPOSE 8079
ENTRYPOINT ["java","-jar","/app.jar"]