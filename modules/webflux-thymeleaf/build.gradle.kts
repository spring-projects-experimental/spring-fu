dependencies {
	api(project(":modules:webflux"))
	api("org.thymeleaf:thymeleaf:3.0.9.RELEASE")
	api("org.thymeleaf:thymeleaf-spring5")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation(project(":modules:webflux-netty"))

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
