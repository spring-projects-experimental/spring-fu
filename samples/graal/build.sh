#!/usr/bin/env sh

cd ../../
./gradlew clean build -x test
cd samples/graal
unzip build/libs/graal-0.0.1.BUILD-SNAPSHOT.jar -d build/libs/graal-0.0.1.BUILD-SNAPSHOT
native-image -H:ReflectionConfigurationFiles=graal.json -Dio.netty.noUnsafe=true -H:+ReportUnsupportedElementsAtRuntime -Dfile.encoding=UTF-8 -cp ".:$(echo build/libs/graal-0.0.1.BUILD-SNAPSHOT/BOOT-INF/lib/*.jar | tr ' ' ':')":build/libs/graal-0.0.1.BUILD-SNAPSHOT/BOOT-INF/classes org.springframework.fu.sample.graal.ApplicationKt
