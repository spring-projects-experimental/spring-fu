dependencies {
	api(project(":modules:webflux"))

	implementation("io.projectreactor.netty:reactor-netty")

	testImplementation(project(":modules:webflux-test-common"))
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
