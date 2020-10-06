#!/usr/bin/env sh

set -e

export GRADLE_OPTS=-Dorg.gradle.native=false
cd spring-fu
./gradlew -q -PrepoUsername=$ARTIFACTORY_USERNAME -PrepoPassword=$ARTIFACTORY_PASSWORD dokkaHtml publish
