import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	application
	id ("com.github.johnrengelman.shadow")
}

application {
	mainClassName = "org.springframework.fu.sample.reactive.ApplicationKt"
}

dependencies {
	compile(project(":core"))
	compile(project(":modules:logging"))
	compile(project(":modules:jackson"))
	compile(project(":modules:mongodb"))
	compile(project(":modules:mustache"))
	compile(project(":modules:test"))
	compile(project(":modules:webflux:netty"))
}

tasks {
	"build" { dependsOn("proguard") }
}
