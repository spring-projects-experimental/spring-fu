import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	id("org.springframework.boot")
}

dependencies {
	implementation(project(":modules:logging-logback"))
	implementation(project(":modules:webflux-jackson"))
	implementation(project(":modules:mongodb"))
	implementation(project(":modules:webflux-netty"))
	implementation(project(":modules:webflux-mustache"))

	testImplementation(project(":modules:test"))
}

configurations.all { exclude(module = "slf4j-simple") }