#todo research regarding layered instalation using eclipse temurin jre as builder to understand the dependancy

FROM openjdk:17

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]