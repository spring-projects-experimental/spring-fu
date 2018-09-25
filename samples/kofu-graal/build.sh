#!/usr/bin/env sh

cd ../../
./gradlew clean build -x test
cd samples/kofu-graal
unzip build/libs/kofu-graal-0.0.1.BUILD-SNAPSHOT.jar -d build/libs/kofu-graal-0.0.1.BUILD-SNAPSHOT
mx -p /home/ezueled/workspace/graal/substratevm native-image --delay-class-initialization-to-runtime=io.netty.handler.codec.http.HttpObjectEncoder,org.springframework.core.io.VfsUtils -H:ReflectionConfigurationFiles=graal-netty.json -Dio.netty.noUnsafe=true -H:+ReportUnsupportedElementsAtRuntime -Dfile.encoding=UTF-8 -cp ".:$(echo build/libs/kofu-graal-0.0.1.BUILD-SNAPSHOT/BOOT-INF/lib/*.jar | tr ' ' ':')":build/libs/kofu-graal-0.0.1.BUILD-SNAPSHOT/BOOT-INF/classes org.springframework.fu.sample.graal.ApplicationKt
