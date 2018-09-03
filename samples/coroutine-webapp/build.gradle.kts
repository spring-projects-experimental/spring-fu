import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	id("org.springframework.boot")
}

kotlin {
	experimental.coroutines = Coroutines.ENABLE
}

dependencies {
	api(project(":modules:webflux-jackson"))
	api(project(":modules:mongodb-coroutine"))
	api(project(":modules:mongodb-embedded"))
	api(project(":modules:webflux"))
	api(project(":modules:webflux-coroutine"))
	api(project(":modules:webflux-mustache"))

	implementation("io.projectreactor.netty:reactor-netty")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
