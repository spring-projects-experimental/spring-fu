#!/usr/bin/env sh

./gradlew clean build
unzip build/libs/jafu-reactive-r2dbc.jar -d build/libs/jafu-reactive-r2dbc

native-image -H:IncludeResources='META-INF/.*.json|META-INF/spring.factories|org/springframework/boot/logging/.*' --allow-incomplete-classpath --delay-class-initialization-to-runtime=io.netty.handler.codec.http.HttpObjectEncoder,org.springframework.core.io.VfsUtils,org.springframework.format.support.DefaultFormattingConversionService -H:ReflectionConfigurationFiles=graal/app.json,graal/boot.json,graal/framework.json,graal/log4j.json,graal/netty.json -Dio.netty.noUnsafe=true -H:+ReportUnsupportedElementsAtRuntime -Dfile.encoding=UTF-8 -cp ".:$(echo build/libs/jafu-reactive-r2dbc/BOOT-INF/lib/*.jar | tr ' ' ':')":build/libs/jafu-reactive-r2dbc/BOOT-INF/classes com.sample.Application
