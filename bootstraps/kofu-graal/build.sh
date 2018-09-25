#!/usr/bin/env sh

./gradlew clean build
unzip build/libs/kofu-graal.jar -d build/libs/kofu-graal

# Netty
native-image -H:ReflectionConfigurationFiles=graal-netty.json -Dio.netty.noUnsafe=true -H:+ReportUnsupportedElementsAtRuntime -Dfile.encoding=UTF-8 -cp ".:$(echo build/libs/kofu-graal/BOOT-INF/lib/*.jar | tr ' ' ':')":build/libs/kofu-graal/BOOT-INF/classes com.example.ApplicationKt

# Jetty
#native-image -H:ReflectionConfigurationFiles=graal-jetty.json -Dio.netty.noUnsafe=true -H:+ReportUnsupportedElementsAtRuntime -Dfile.encoding=UTF-8 -cp ".:$(echo build/libs/kofu-graal/BOOT-INF/lib/*.jar | tr ' ' ':')":build/libs/kofu-graal/BOOT-INF/classes com.example.ApplicationKt