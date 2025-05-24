FROM gradle:8.12-jdk21-corretto AS buildsystem
WORKDIR /news-bot
COPY . .
RUN gradle bootJar --no-daemon -x test

FROM amazoncorretto:21
WORKDIR /news-bot

COPY --from=buildsystem /news-bot/build/libs/ZapolyarnyNewsBot-0.0.1-SNAPSHOT.jar NewsBot.jar
CMD [ "java", "-jar", "NewsBot.jar"]