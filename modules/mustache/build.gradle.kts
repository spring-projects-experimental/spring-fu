dependencies {
	compile(project(":modules:webflux"))
	compile("com.samskivert:jmustache")

	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testCompile("org.springframework:spring-test")
}