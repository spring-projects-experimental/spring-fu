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
	implementation(project(":modules:logging"))
	implementation(project(":modules:jackson"))
	implementation(project(":modules:mongodb-coroutines"))
	implementation(project(":modules:mustache"))
	implementation(project(":modules:test"))
	implementation(project(":modules:webflux-netty"))
	implementation(project(":modules:webflux-coroutines"))
}
