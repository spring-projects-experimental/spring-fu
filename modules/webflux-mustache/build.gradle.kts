dependencies {
	api(project(":modules:webflux"))
	api("org.springframework.boot:spring-boot-starter-mustache")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
