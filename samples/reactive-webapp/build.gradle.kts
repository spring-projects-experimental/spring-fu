import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	application
	id ("com.github.johnrengelman.shadow")
}

application {
	mainClassName = "org.springframework.fu.sample.reactive.ApplicationKt"
}

dependencies {
    implementation(project(":modules:logging-logback"))
    implementation(project(":modules:jackson"))
    implementation(project(":modules:mongodb"))
    implementation(project(":modules:mustache"))
    implementation(project(":modules:webflux-netty"))

    testImplementation(project(":modules:test"))
}

configurations.all { exclude(module = "slf4j-simple") }