FROM adoptopenjdk/openjdk11:ubi
ADD target/crossroad-parser-service.jar /app.jar
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=docker","/app.jar"]