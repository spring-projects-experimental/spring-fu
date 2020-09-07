#!/usr/bin/env bash

./gradlew -x javadoc -x dokka build publishToMavenLocal
cd samples
./gradlew build
cd kofu-petclinic-jdbc
./mvnw package