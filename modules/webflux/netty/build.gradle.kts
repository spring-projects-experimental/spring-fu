dependencies {
	api(project(":modules:webflux"))

	implementation("io.projectreactor.netty:reactor-netty:0.8.0.BUILD-SNAPSHOT")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation("io.projectreactor:reactor-test")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

base {
	archivesBaseName = "spring-fu-webflux-netty"
}
