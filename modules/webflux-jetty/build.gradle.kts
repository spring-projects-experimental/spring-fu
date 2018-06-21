dependencies {
	api(project(":modules:webflux"))

	implementation("org.eclipse.jetty:jetty-server")
	implementation("org.eclipse.jetty:jetty-servlet")
	implementation("org.slf4j:slf4j-api:1.7.25")

	testImplementation(project(":modules:webflux-test-common"))

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}