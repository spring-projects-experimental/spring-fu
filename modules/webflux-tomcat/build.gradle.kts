dependencies {
	api(project(":modules:webflux"))

	implementation("org.apache.tomcat.embed:tomcat-embed-core")

	testImplementation(project(":modules:webflux-test-common"))

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
