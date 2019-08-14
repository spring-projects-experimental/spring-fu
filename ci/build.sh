#!/usr/bin/env sh

set -e

export GRADLE_OPTS=-Dorg.gradle.native=false
cd spring-fu
./gradlew -q build publishToMavenLocal -PisCI=true
cd samples
./gradlew -q build -PisCI=true
