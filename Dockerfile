FROM eclipse-temurin:17-jre-alpine AS runtime
RUN apk update && apk add --no-cache yt-dlp ffmpeg

FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /anime-dl
COPY gradlew ./
COPY gradle/wrapper ./gradle/wrapper
RUN ./gradlew  --no-daemon --version
COPY settings.gradle .
COPY anime-dl/build.gradle ./anime-dl/
COPY gradle/libs.versions.toml ./gradle/libs.versions.toml
COPY gradle.properties ./
RUN ./gradlew --no-daemon dependencies
COPY anime-dl/src ./anime-dl/src/
RUN ./gradlew install

FROM runtime
WORKDIR /anime-dl
COPY --from=build /anime-dl/anime-dl/build/install/anime-dl ./
ENTRYPOINT ["sh"]

