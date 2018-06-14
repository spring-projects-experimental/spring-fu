dependencies {
	api(project(":modules:webflux"))

	implementation("io.undertow:undertow-core")

	testImplementation(project(":modules:webflux-test-common"))

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
