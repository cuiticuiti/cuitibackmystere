FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

# 🔥 FORZAMOS permisos sí o sí
RUN chmod 755 gradlew

RUN ./gradlew build -x test

COPY build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
