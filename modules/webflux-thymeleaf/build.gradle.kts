dependencies {
	api(project(":modules:webflux"))
	api("org.thymeleaf:thymeleaf")
	api("org.thymeleaf:thymeleaf-spring5")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor.netty:reactor-netty")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
