import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	application
	id ("com.github.johnrengelman.shadow")
}

application {
	mainClassName = "org.springframework.fu.sample.coroutines.ApplicationKt"
}

kotlin {
	experimental.coroutines = Coroutines.ENABLE
}

dependencies {
	implementation(project(":modules:logging-logback"))
	implementation(project(":modules:jackson"))
	implementation(project(":modules:mongodb-coroutine"))
	implementation(project(":modules:mustache"))
	implementation(project(":modules:test"))
	implementation(project(":modules:webflux-netty"))
	implementation(project(":modules:webflux-coroutine"))
}

configurations.all { exclude(module = "slf4j-simple") }