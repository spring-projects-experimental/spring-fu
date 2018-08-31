dependencies {
	api(project(":modules:webflux"))
	api("org.thymeleaf:thymeleaf")
	api("org.springframework.boot:spring-boot-starter-thymeleaf")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
