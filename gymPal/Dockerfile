FROM openjdk:11
WORKDIR '/app'
ADD target/gymApp-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
COPY . .
ENTRYPOINT ["java", "-jar", "gymApp-0.0.1-SNAPSHOT.jar"]