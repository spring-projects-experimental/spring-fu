#!/usr/bin/env sh

./gradlew clean build
unzip build/libs/kofu-reactive-graal.jar -d build/libs/kofu-reactive-graal

native-image --delay-class-initialization-to-runtime=io.netty.handler.codec.http.HttpObjectEncoder,org.springframework.core.io.VfsUtils -H:ReflectionConfigurationFiles=graal.json -Dio.netty.noUnsafe=true -H:+ReportUnsupportedElementsAtRuntime -Dfile.encoding=UTF-8 -cp ".:$(echo build/libs/kofu-reactive-graal/BOOT-INF/lib/*.jar | tr ' ' ':')":build/libs/kofu-reactive-graal/BOOT-INF/classes com.sample.ApplicationKt
