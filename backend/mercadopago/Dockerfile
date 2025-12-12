FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew bootJar -x test

# mover el jar generado a app.jar
RUN mv build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
