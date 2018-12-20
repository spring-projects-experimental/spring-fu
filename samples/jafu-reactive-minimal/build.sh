#!/usr/bin/env sh

../jafu-reactive-minimal/gradlew clean build -Pgraal=true
unzip build/libs/jafu-reactive-minimal.jar -d build/libs/jafu-reactive-minimal

native-image -H:IncludeResources='META-INF/.*.json|META-INF/spring.factories|org/springframework/boot/logging/.*' --allow-incomplete-classpath --delay-class-initialization-to-runtime=io.netty.handler.codec.http.HttpObjectEncoder,org.springframework.core.io.VfsUtils,org.springframework.format.support.DefaultFormattingConversionService -H:ReflectionConfigurationFiles=graal/app.json,graal/boot.json,graal/framework.json,graal/log4j.json,graal/netty.json -Dio.netty.noUnsafe=true -H:+ReportUnsupportedElementsAtRuntime -Dfile.encoding=UTF-8 -cp ".:$(echo build/libs/jafu-reactive-minimal/BOOT-INF/lib/*.jar | tr ' ' ':')":build/libs/jafu-reactive-minimal/BOOT-INF/classes com.sample.Application
