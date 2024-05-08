FROM azul/zulu-openjdk:17-latest AS builder
WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew spotlessApply
RUN ./gradlew clean build -x test -x asciidoctor

FROM azul/zulu-openjdk:17-latest
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]