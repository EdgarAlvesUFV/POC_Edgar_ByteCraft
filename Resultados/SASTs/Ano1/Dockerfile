FROM openjdk:17-jdk-alpine

# instalando o Gradle dentro do container
ARG GRADLE_VERSION=8.10.2
RUN apk add --no-cache curl; \
    curl https://downloads.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip > gradle.zip; \
    unzip gradle.zip && rm gradle.zip; \
    apk del curl
ENV PATH=${PATH}:/gradle-${GRADLE_VERSION}/bin