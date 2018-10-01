#!/usr/bin/env sh

set -e

export GRADLE_OPTS=-Dorg.gradle.native=false
cd bootstraps/kofu-coroutines
./gradlew -q build --stacktrace
cd ../kofu-reactive
./gradlew -q build --stacktrace
cd ../kofu-graal
./gradlew -q build --stacktrace
cd spring-fu
./gradlew -q -PrepoUsername=$ARTIFACTORY_USERNAME -PrepoPassword=$ARTIFACTORY_PASSWORD publish
