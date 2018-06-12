dependencies {
	api(project(":modules:webflux"))
	api("com.fasterxml.jackson.core:jackson-databind")
	api("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation(project(":modules:webflux-netty"))

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
