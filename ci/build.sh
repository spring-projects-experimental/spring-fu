#!/usr/bin/env sh

set -e

export GRADLE_OPTS=-Dorg.gradle.native=false
cd spring-fu
./gradlew -q build publishToMavenLocal --stacktrace
cd samples
./gradlew -q build --stacktrace
cd jafu-reactive-minimal
./mvnw clean install
cd ../jafu-reactive-r2dbc
./mvnw clean install