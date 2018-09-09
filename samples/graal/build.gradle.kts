import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	id("org.springframework.boot")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation(project(":kofu"))

	// Workaround for https://github.com/oracle/graal/issues/655
	implementation("javax.servlet:javax.servlet-api")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

configurations.all {
	exclude(module = "netty-transport-native-epoll")
	exclude(module = "netty-transport-native-unix-common")
	exclude(module = "netty-codec-http2")
	exclude(module = "hibernate-validator")
}
