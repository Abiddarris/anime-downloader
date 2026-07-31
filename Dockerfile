FROM eclipse-temurin:17-jdk-alpine AS build
RUN apk update && apk add --no-cache \
    yt-dlp \
    ffmpeg
WORKDIR /anime-dl
COPY gradle.properties gradlew .
COPY gradle/wrapper ./gradle/wrapper
RUN ./gradlew  --no-daemon --version
COPY settings.gradle .
COPY anime-dl/build.gradle ./anime-dl/
COPY gradle/libs.versions.toml ./gradle/libs.versions.toml
RUN ./gradlew --no-daemon dependencies
COPY anime-dl/src ./anime-dl/src/
RUN ./gradlew install
COPY anime-dl/build/install/anime-dl /anime-dl-install
WORKDIR /anime-dl-install
ENTRYPOINT ["sh"]

