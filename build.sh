#!/usr/bin/env bash

./gradlew -x javadoc build publishToMavenLocal
cd samples
./gradlew build
