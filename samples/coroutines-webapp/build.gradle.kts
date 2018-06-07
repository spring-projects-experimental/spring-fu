import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	application
	id ("com.github.johnrengelman.shadow")
}

application {
	mainClassName = "org.springframework.fu.sample.coroutines.ApplicationKt"
}

dependencies {
	compile(project(":core"))
	compile(project(":modules:logging"))
	compile(project(":modules:jackson"))
	compile(project(":modules:mongodb"))
	compile(project(":modules:mongodb:coroutines"))
	compile(project(":modules:mustache"))
	compile(project(":modules:test"))
	compile(project(":modules:webflux:netty"))
	compile(project(":modules:webflux:coroutines"))
}

kotlin {
	experimental.coroutines = Coroutines.ENABLE
}