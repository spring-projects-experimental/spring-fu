#!/usr/bin/env sh

set -e

export GRADLE_OPTS=-Dorg.gradle.native=false
cd spring-fu
./gradlew -q build --stacktrace
cd bootstraps/coroutine-webapp
./gradlew -q build --stacktrace
cd ../reactive-webapp
./gradlew -q build --stacktrace