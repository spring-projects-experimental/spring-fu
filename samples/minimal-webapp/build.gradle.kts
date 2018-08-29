import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	id("org.springframework.boot")
}

dependencies {
	api("org.springframework.boot:spring-boot-starter")

	api(project(":modules:webflux-netty"))

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.springframework:spring-test")
}
