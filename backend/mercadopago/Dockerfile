FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

# 🔥 ESTA LÍNEA ES LA CLAVE
RUN chmod +x gradlew

RUN ./gradlew build -x test

COPY build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
