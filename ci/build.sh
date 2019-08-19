#!/usr/bin/env sh

set -e

export GRADLE_OPTS=-Dorg.gradle.native=false
cd spring-fu
./gradlew build publishToMavenLocal -PisCI=true
cd samples
./gradlew build -PisCI=true
