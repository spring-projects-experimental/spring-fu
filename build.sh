#!/usr/bin/env bash

set -e

./gradlew -x javadoc -x dokkaHtml build publishToMavenLocal
cd samples
./gradlew build
cd kofu-petclinic-jdbc
./mvnw package