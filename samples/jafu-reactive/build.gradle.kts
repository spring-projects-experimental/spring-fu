import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
	id("org.springframework.boot")
}

dependencies {
	implementation(project(":jafu"))

	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
