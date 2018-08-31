dependencies {
	api(project(":modules:webflux"))
	api("org.springframework.boot:spring-boot-starter-json")
	api("com.fasterxml.jackson.module:jackson-module-kotlin")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
