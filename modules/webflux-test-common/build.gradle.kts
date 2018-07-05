dependencies {
	api(project(":modules:webflux"))

	implementation("org.junit.jupiter:junit-jupiter-api")
	implementation("org.springframework:spring-test")
	implementation("io.projectreactor:reactor-test")
	implementation("io.projectreactor.netty:reactor-netty") // For the client
}