# Multistage build
# The first stage is the build stage. It uses the gradle image as the base image and builds the project.
# The second stage is to get the apk file from the first stage and copy it to the current directory and serve an nginx server.
# The nginx server is used to serve the apk file.
# The apk file is served on port 8000

# Build stage
FROM gradle:7.4-jdk11 as build

# Install the Android SDK
RUN apt-get update && apt-get install -y wget unzip dos2unix
RUN wget https://dl.google.com/android/repository/commandlinetools-linux-6858069_latest.zip
RUN unzip commandlinetools-linux-6858069_latest.zip
RUN mkdir -p /opt/android-sdk/cmdline-tools
RUN mv cmdline-tools /opt/android-sdk/cmdline-tools/latest
RUN rm commandlinetools-linux-6858069_latest.zip
ENV ANDROID_HOME /opt/android-sdk
ENV ANDROID_SDK_ROOT /opt/android-sdk
ENV PATH $PATH:$ANDROID_HOME/cmdline-tools/latest/bin
RUN yes | sdkmanager --licenses
RUN sdkmanager "platform-tools" "platforms;android-30" "build-tools;30.0.3"

# Copy the current directory contents into the container at /app
COPY . /app

# Set the working directory to /app
WORKDIR /app

# Set environment variables from arg variables
ARG BACK_API_URL

ENV BACK_API_URL=${BACK_API_URL}

RUN chmod +x gradlew
# change gradlew eol to unix
RUN dos2unix gradlew

# Clean and build the project
RUN ./gradlew clean build

# Serve stage
FROM nginx:latest as serve

# Copy the apk file from the build stage to the current directory
COPY --from=build /app/app/build/outputs/apk/debug/app-debug.apk /usr/share/nginx/html/app-debug.apk