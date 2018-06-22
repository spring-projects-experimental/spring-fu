import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	id("org.springframework.boot")
}

kotlin {
	experimental.coroutines = Coroutines.ENABLE
}

dependencies {
	implementation(project(":modules:logging-logback"))
	implementation(project(":modules:webflux-jackson"))
	implementation(project(":modules:mongodb-coroutine"))
	implementation(project(":modules:mustache"))
	implementation(project(":modules:test"))
	implementation(project(":modules:webflux-netty"))
	implementation(project(":modules:webflux-coroutine"))
}

configurations.all { exclude(module = "slf4j-simple") }