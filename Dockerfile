FROM adoptopenjdk/openjdk11:jre-11.0.9_11.1-alpine
ADD target/app.jar /home/app.jar
WORKDIR "/home"
EXPOSE 4202
CMD ["java", "-Dspring.application.name=schedulerservice", "-Xmx256m", "-jar", "./app.jar"]
