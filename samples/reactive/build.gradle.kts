import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	id("org.springframework.boot")
}

dependencies {

	api("org.springframework.boot:spring-boot-starter-webflux")
	api("org.springframework.boot:spring-boot-starter-mustache")
	api("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	api(project(":kofu"))
	api("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
