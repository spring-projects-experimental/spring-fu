dependencies {
	api(project(":modules:webflux"))
	api("com.samskivert:jmustache")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.springframework:spring-test")
	testImplementation(project(":modules:webflux-netty"))

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
