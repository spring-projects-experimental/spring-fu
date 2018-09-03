import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	id("org.springframework.boot")
}

dependencies {
	api(project(":modules:webflux"))

	// Remove when Graal RC6 will be released
	implementation("org.aspectj:aspectjweaver")
	implementation("com.jcraft:jzlib:1.1.3")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

configurations.all {
	exclude(module = "netty-transport-native-epoll")
	exclude(module = "netty-transport-native-unix-common")
}
