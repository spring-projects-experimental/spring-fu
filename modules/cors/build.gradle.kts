dependencies {
	api(project(":modules:webflux"))

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation(project(":modules:webflux-netty"))

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
