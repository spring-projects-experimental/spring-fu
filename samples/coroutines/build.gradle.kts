import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	id("org.springframework.boot")
}

kotlin {
	experimental.coroutines = Coroutines.ENABLE
}

dependencies {
	api("org.springframework.boot:spring-boot-starter-mustache")
	api(project(":kofu"))
	api(project(":starters:webflux-coroutines"))
	api(project(":starters:mongodb-coroutines"))
	api("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
