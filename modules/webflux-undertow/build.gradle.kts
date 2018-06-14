dependencies {
	api(project(":modules:webflux"))

	implementation("io.undertow:undertow-core")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("io.projectreactor.netty:reactor-netty:") // For the client

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
