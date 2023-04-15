# Use a slim version of Ubuntu 23.04 as the base image
FROM ubuntu:23.04 AS base

# Update the package list and install wget
RUN apt-get update && apt-get install -y wget gnupg

RUN wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN apt-get install ./google-chrome-stable_current_amd64.deb -y

RUN apt-get update && apt-get install -y google-chrome-stable
RUN apt-get install ia32-libs-gtk ia32-lib
# Download and install the latest version of Google Chrome


# Use Maven 3.8.6 and OpenJDK 18 to build the application
FROM maven:3.8.6-openjdk-18-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /app/src/
RUN mvn clean package -Dmaven.test.skip

# Use OpenJDK 18 to run the application
FROM openjdk:18-slim AS final
WORKDIR /app
COPY --from=build /app/target/zalo-bot-1.0.0.jar app.jar
EXPOSE 7171
CMD ["java", "-jar", "app.jar"]
