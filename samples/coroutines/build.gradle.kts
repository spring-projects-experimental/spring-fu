plugins {
	id("org.springframework.boot")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation(project(":kofu"))
	implementation(project(":starters:webflux-coroutines"))
	implementation(project(":starters:data-mongodb-coroutines"))
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
