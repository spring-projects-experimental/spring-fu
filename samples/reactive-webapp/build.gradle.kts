import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	id("org.springframework.boot")
}

dependencies {
	api(project(":modules:webflux-jackson"))
	api(project(":modules:mongodb"))
	api(project(":modules:mongodb-embedded"))
	api(project(":modules:webflux"))
	api(project(":modules:webflux-mustache"))

	implementation("io.projectreactor.netty:reactor-netty")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
