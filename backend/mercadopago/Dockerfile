FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN ./gradlew build -x test

COPY build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
